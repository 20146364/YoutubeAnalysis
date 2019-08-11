package hungpt.deverlopment.youtubeanalysis.fragment.tab;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hungpt.deverlopment.youtubeanalysis.R;
import hungpt.deverlopment.youtubeanalysis.commons.DeveloperKey;
import hungpt.deverlopment.youtubeanalysis.network.HttpGetDataAsyntask;
import hungpt.deverlopment.youtubeanalysis.network.HttpPostDataAsyntask;
import hungpt.deverlopment.youtubeanalysis.utils.ConvertDataYoutube;

public class FragmentAnalysis extends Fragment implements OnChartValueSelectedListener {
    private View view;
    private TextView numberComment;
    private int positive = 0, negative = 0;
    private TextView txt_number_positive, txt_number_negative;
    private ProgressBar progress;
    private RelativeLayout rl_numberComment;
    private LinearLayout ln_info;
    private PieChart mChart;
    private Typeface tf;
    private int CMT;
    private String stringCMT = "";
    private String ID;
    private ConvertDataYoutube covert;
    private String nextPageToken = null;
    private DeveloperKey developerKey = new DeveloperKey();
    private List<String> stringList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_analysis, container, false);
        getDataBundle();
        initView(view);
        return view;
    }

    private void getDataBundle() {
        if (getArguments() != null) {
            ID = getArguments().getString("ID");
            CMT = getArguments().getInt("cmt");
        }
    }

    private void initView(View view) {
        covert = new ConvertDataYoutube();
        numberComment = view.findViewById(R.id.numberComment);
        txt_number_positive = view.findViewById(R.id.txt_number_positive);
        txt_number_negative = view.findViewById(R.id.txt_number_negative);
        progress = view.findViewById(R.id.progress);
        rl_numberComment = view.findViewById(R.id.rl_numberComment);
        ln_info = view.findViewById(R.id.ln_info);
        mChart = view.findViewById(R.id.chartRevenueMonth);
        setView();
    }

    private void setView() {
        numberComment.setText(covert.formatNumber(String.valueOf(CMT)));
        getDataComment();
    }

    private void getDataComment() {
        new HttpGetDataAsyntask(new HttpGetDataAsyntask.OnResponse() {
            @Override
            public void start() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void failed() {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void success(String result) throws JSONException {
                JSONObject root = new JSONObject(result);
                try {
                    nextPageToken = root.getString("nextPageToken");
                } catch (Exception e) {
                    nextPageToken = "";
                }

                JSONArray items = root.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject object = items.getJSONObject(i);
                    JSONObject obj = object.getJSONObject("snippet");
                    JSONObject topLevelComment = obj.getJSONObject("topLevelComment");
                    JSONObject snippet = topLevelComment.getJSONObject("snippet");
                    String textDisplay = snippet.getString("textDisplay");
                    if (i == 0) {
                        stringCMT += '"' + textDisplay.replaceAll("\"", "'") + '"';
                    } else {
                        stringCMT += "," + '"' + textDisplay.replaceAll("\"", "'") + '"';
                    }

                }

                stringCMT = "[" + stringCMT + "]";

                new HttpPostDataAsyntask(formatStringJson(stringCMT), new HttpPostDataAsyntask.OnResponse() {
                    @Override
                    public void failed() {
                        progress.setVisibility(View.GONE);
                        initChart();
                    }

                    @Override
                    public void success(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            JSONArray array = object.getJSONArray("result");

                            for (int m = 0; m < array.length(); m++) {

                                if (array.get(m).toString().equals("0")) {
                                    negative++;
                                } else {
                                    positive++;
                                }
                            }

                            progress.setVisibility(View.GONE);
                            float x = (float) positive / (float) (positive + negative);
                            int i = (int) (x * CMT);
                            txt_number_negative.setText(covert.formatNumber(String.valueOf(CMT - i)));
                            txt_number_positive.setText(covert.formatNumber(String.valueOf(i)));
                            initChart();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.43.32:5000/nlp/classification");


            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, developerKey.getListCommentByID(ID, nextPageToken));
    }

    private String formatStringJson(String content) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(content);
        return gson.toJson(je);
    }

    private void initChart() {
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);
        tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");

        mChart.setCenterTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf"));
        mChart.setCenterText(generateCenterSpannableText());

        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.setOnChartValueSelectedListener(this);

        setData();

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("Tỷ lệ thái độ \n các bình luận");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 15, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, s.length(), 0);
        return s;
    }

    private void setData() {


        float x = (float) positive / (float) (positive + negative);

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();


        entries.add(new PieEntry(x, "Tích cực"));
        entries.add(new PieEntry(1 - x, "Tiêu cực"));

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();


    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
