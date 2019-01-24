Butter Knife FindView Plugin
============

Field and method binding for Android views which uses plugin to generate boilerplate
code for you.

 * Eliminate `findViewById` calls by using `@CBind` on fields.
 * Group multiple views in a list or array. Operate on all of them at once with actions,
   setters, or properties.
 * Eliminate anonymous inner-classes for listeners by annotating methods with `@COnClick` and others.
 * Eliminate resource lookups by using resource annotations on fields.

```java
public class DemoActivity
        extends Activity {

    @CBind("com.tts.hybird.train.R.id.tv_TitleBarText")
    TextView tvTitleBarText;
    @CBind("com.tts.hybird.train.R.id.tv_TitleBarTextSmall")
    TextView tvTitleBarTextSmall;
    @CBind("com.tts.hybird.train.R.id.iv_TitleBarBack")
    ImageView ivTitleBarBack;
    @CBind("com.tts.hybird.train.R.id.iv_TitleBarRightBtn")
    ImageView ivTitleBarRightBtn;
    @CBind("com.tts.hybird.train.R.id.tv_TitleBarRightText")
    TextView tvTitleBarRightText;
    @CBind("com.tts.hybird.train.R.id.layout_TitleBarRight")
    LinearLayout layoutTitleBarRight;
    @CBind("com.tts.hybird.train.R.id.root_title_rl")
    RelativeLayout rootTitleRl;
    @CBind("com.tts.hybird.train.R.id.m_train_tv_pre_sale_day_count")
    TextView mTrainTvPreSaleDayCount;
    @CBind("com.tts.hybird.train.R.id.m_train_layout_notice")
    LinearLayout mTrainLayoutNotice;
    @CBind("com.tts.hybird.train.R.id.m_train_vs_cal_one")
    ViewStub mTrainVsCalOne;
    @CBind("com.tts.hybird.train.R.id.m_train_vs_cal_two")
    ViewStub mTrainVsCalTwo;
    @CBind("com.tts.hybird.train.R.id.m_train_vs_cal_three")
    ViewStub mTrainVsCalThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m_train_activity_select_date)
        CButterKnife.bind(this);

    }

 

    @COnClick({"com.tts.hybird.train.R.id.tv_TitleBarText", "com.tts.hybird.train.R.id.tv_TitleBarTextSmall", "com.tts.hybird.train.R.id.iv_TitleBarBack", "com.tts.hybird.train.R.id.iv_TitleBarRightBtn", "com.tts.hybird.train.R.id.tv_TitleBarRightText"})
    public void onViewClicked(View view, final String idName) {
        switch (idName) {
            case "com.tts.hybird.train.R.id.tv_TitleBarText":
                break;
            case "com.tts.hybird.train.R.id.tv_TitleBarTextSmall":
                break;
            case "com.tts.hybird.train.R.id.iv_TitleBarBack":
                break;
            case "com.tts.hybird.train.R.id.iv_TitleBarRightBtn":
                break;
            case "com.tts.hybird.train.R.id.tv_TitleBarRightText":
                break;
        }
    }

```

Download 
--------

browse Repositories by 'Android ButterKnife viewbindgenerate'


Thank
-------
Thank @TomasKypta with his android-butterknife-zelezny
  

