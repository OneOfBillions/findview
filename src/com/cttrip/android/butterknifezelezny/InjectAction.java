package com.cttrip.android.butterknifezelezny;

import com.cttrip.android.butterknifezelezny.butterknife.ButterKnifeFactory;
import com.cttrip.android.butterknifezelezny.butterknife.IButterKnife;
import com.cttrip.android.butterknifezelezny.common.Definitions;
import com.cttrip.android.butterknifezelezny.common.Utils;
import com.cttrip.android.butterknifezelezny.form.EntryList;
import com.cttrip.android.butterknifezelezny.iface.ICancelListener;
import com.cttrip.android.butterknifezelezny.iface.IConfirmListener;
import com.cttrip.android.butterknifezelezny.model.Element;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;

import javax.swing.*;
import java.util.ArrayList;

public class InjectAction extends BaseGenerateAction implements IConfirmListener, ICancelListener {

    protected JFrame mDialog;
    protected static final Logger log = Logger.getInstance(InjectAction.class);

    @SuppressWarnings("unused")
    public InjectAction() {
        super(null);
    }

    @SuppressWarnings("unused")
    public InjectAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(final PsiClass targetClass) {
        IButterKnife butterKnife = ButterKnifeFactory.findButterKnifeForPsiElement(targetClass.getProject(), targetClass);

        return (butterKnife != null && super.isValidForClass(targetClass) && Utils.findAndroidSDK() != null && !(targetClass instanceof PsiAnonymousClass));
    }

    @Override
    public boolean isValidForFile(Project project, Editor editor, PsiFile file) {
        IButterKnife butterKnife = ButterKnifeFactory.findButterKnifeForPsiElement(project, file);

        return (butterKnife != null && super.isValidForFile(project, editor, file) && Utils.getLayoutFileFromCaret(editor, file) != null);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        actionPerformedImpl(project, editor);
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private String packageName;

    @Override
    public void actionPerformedImpl(Project project, Editor editor) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiFile layout = Utils.getLayoutFileFromCaret(editor, file);
        PsiFile androidManifestFile = Utils.resolveFile(editor,file,"BuildConfig.java");

          String str = androidManifestFile.getText();
           if(str.indexOf("APPLICATION_ID = \"")>0){
                    String subString  = str.substring(str.indexOf("APPLICATION_ID = \"")+18);
                    packageName =  subString.substring(0,subString.indexOf("\";"));
          }



//        if(result!=null && !result.isEmpty()){
//            packageName = result.get(0);
//        }

        if (layout == null) {
            Utils.showErrorNotification(project, "No layout found");
            return; // no layout found
        }


        ArrayList<Element> elements = Utils.getIDsFromLayout(layout);
        if (!elements.isEmpty()) {
            showDialog(project, editor, elements);
        } else {
            Utils.showErrorNotification(project, "No IDs found in layout");
        }
    }

    public void onConfirm(Project project, Editor editor, ArrayList<Element> elements, String fieldNamePrefix, boolean createHolder, boolean splitOnclickMethods) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
        PsiFile layout = Utils.getLayoutFileFromCaret(editor, file);

        closeDialog();


        if (Utils.getInjectCount(elements) > 0 || Utils.getClickCount(elements) > 0) { // generate injections
            new InjectWriter(file, getTargetClass(editor, file), "Generate Injections", elements, layout.getName(), fieldNamePrefix, createHolder, splitOnclickMethods).setPackageName(packageName).execute();
        } else { // just notify user about no element selected
            Utils.showInfoNotification(project, "No injection was selected");
        }

    }

    public void onCancel() {
        closeDialog();
    }

    protected void showDialog(Project project, Editor editor, ArrayList<Element> elements) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
        PsiClass clazz = getTargetClass(editor, file);

        final IButterKnife butterKnife = ButterKnifeFactory.findButterKnifeForPsiElement(project, file);
        if (clazz == null || butterKnife == null) {
            return;
        }

        // get parent classes and check if it's an adapter
        boolean createHolder = false;
        PsiReferenceList list = clazz.getExtendsList();
        if (list != null) {
            for (PsiJavaCodeReferenceElement element : list.getReferenceElements()) {
                if (Definitions.adapters.contains(element.getQualifiedName())) {
                    createHolder = true;
                }
            }
        }

        // get already generated injections
        ArrayList<String> ids = new ArrayList<String>();
        PsiField[] fields = clazz.getAllFields();
        String[] annotations;
        String id;

        for (PsiField field : fields) {
            annotations = field.getFirstChild().getText().split(" ");

            for (String annotation : annotations) {
                id = Utils.getInjectionID(butterKnife, annotation.trim());
                if (!Utils.isEmptyString(id)) {
                    ids.add(id);
                }
            }
        }

        EntryList panel = new EntryList(project, editor, elements, ids, createHolder, this, this);

        mDialog = new JFrame();
        mDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mDialog.getRootPane().setDefaultButton(panel.getConfirmButton());
        mDialog.getContentPane().add(panel);
        mDialog.pack();
        mDialog.setLocationRelativeTo(null);
        mDialog.setVisible(true);
    }

    protected void closeDialog() {
        if (mDialog == null) {
            return;
        }

        mDialog.setVisible(false);
        mDialog.dispose();
    }
}
