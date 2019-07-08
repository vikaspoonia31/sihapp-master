package cz.martykan.forecastie.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;

import cz.martykan.forecastie.R;
import cz.martykan.forecastie.models.Weather;
import cz.martykan.forecastie.tasks.ParseResult;
import cz.martykan.forecastie.utils.UnitConvertor;


public class GraphActivity2 extends BaseActivity {

    SharedPreferences sp;

    int theme;

    ArrayList<Weather> weatherList = new ArrayList<>();

    List<JSONObject> json = new ArrayList<JSONObject>();
    private LineChart angleChart;

    float minTemp = 100000;
    float maxTemp = 0;

    float minRain = 100000;
    float maxRain = 0;

    float minPressure = 100000;
    float maxPressure = 0;

    float minWindSpeed = 100000;
    float maxWindSpeed = 0;

    private String labelColor = "#000000";
    private String lineColor = "#333333";

    private boolean darkTheme = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Integer.toString(weatherList.size()), "sas");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs.getString("theme", "fresh")));
        darkTheme = theme == R.style.AppTheme_NoActionBar_Dark ||
                theme == R.style.AppTheme_NoActionBar_Black ||
                theme == R.style.AppTheme_NoActionBar_Classic_Dark ||
                theme == R.style.AppTheme_NoActionBar_Classic_Black;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph2);

        Toolbar toolbar = findViewById(R.id.graph2_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView rainTextView = findViewById(R.id.graph2RainTextView);
        TextView pressureTextView = findViewById(R.id.graph2PressureTextView);
        TextView windSpeedTextView = findViewById(R.id.graph2WindSpeedTextView);

        if (darkTheme) {
            toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay_Dark);
            labelColor = "#FFFFFF";
            lineColor = "#FAFAFA";

            rainTextView.setTextColor(Color.parseColor(labelColor));
            pressureTextView.setTextColor(Color.parseColor(labelColor));
            windSpeedTextView.setTextColor(Color.parseColor(labelColor));
        }

        sp = PreferenceManager.getDefaultSharedPreferences(GraphActivity2.this);
        String lastLongterm = sp.getString("lastLongterm", "");

        if (parseLongTermJson(lastLongterm) == ParseResult.OK) {
            json = loadJSONFromAsset(this);

            angleGraph(json);
            rainGraph();
            pressureGraph();
            windSpeedGraph();
        } else {
            Snackbar.make(findViewById(android.R.id.content), R.string.msg_err_parsing_json, Snackbar.LENGTH_LONG).show();
        }
    }
    public List<JSONObject> loadJSONFromAsset(Context context) {
        List<JSONObject> data = new ArrayList<JSONObject>();
        try {
            InputStream is = context.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++)
                data.add(jsonArray.getJSONObject(i));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }
    private void angleGraph(List<JSONObject> data) {
        Log.d("sas", data.toString());
        LineData angleData = new LineData();
        angleChart = (LineChart) findViewById(R.id.chart);
        final ArrayList<String> labels = new ArrayList<String>();
        try {
            // code to get individual arrays starts
            // initialized variables
            List<Entry> valsAngleX = new ArrayList<Entry>();
            List<Entry> valsAngleY = new ArrayList<Entry>();
            List<Entry> valsAngleZ = new ArrayList<Entry>();

            for (int i = 0; i < data.size(); i++) {
                JSONObject item = data.get(i);
                double angleX = (double) item.get("angle_x");
                double angley = (double) item.get("angle_y");
                double angleZ = (double) item.get("angle_z");
                valsAngleX.add(new Entry((float) i, (float) angleX));
                valsAngleY.add(new Entry((float) i, (float) angley));
                valsAngleZ.add(new Entry((float) i, (float) angleZ));
                labels.add(i, item.get("TimeStamp").toString());
            }
            Log.d("label", labels.toString());

            LineDataSet setAngleX = new LineDataSet(valsAngleX, "Angle X");
            setAngleX.setAxisDependency(AxisDependency.LEFT);
            setAngleX.setColor(Color.rgb(255, 241, 46));
            setAngleX.setDrawCircles(false);
            setAngleX.setLineWidth(2f);
            setAngleX.setCircleRadius(3f);
            setAngleX.setFillAlpha(255);
            setAngleX.setDrawFilled(true);
            setAngleX.setFillColor(Color.WHITE);
            setAngleX.setHighLightColor(Color.rgb(244, 117, 117));
            setAngleX.setDrawCircleHole(false);
            setAngleX.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    // change the return value here to better understand the effect
                    // return 0;
                    return angleChart.getAxisLeft().getAxisMinimum();
                }
            });
            LineDataSet setAngleY = new LineDataSet(valsAngleY, "Angle Y");
            setAngleY.setAxisDependency(AxisDependency.LEFT);
            setAngleY.setColor(Color.rgb(233, 137, 122));
            setAngleY.setDrawCircles(false);
            setAngleY.setLineWidth(2f);
            setAngleY.setCircleRadius(3f);
            setAngleY.setFillAlpha(255);
            setAngleY.setDrawFilled(true);
            setAngleY.setFillColor(Color.WHITE);
            setAngleY.setDrawCircleHole(false);
            setAngleY.setHighLightColor(Color.rgb(244, 117, 117));
            setAngleY.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    // change the return value here to better understand the effect
                    // return 600;
                    return angleChart.getAxisLeft().getAxisMaximum();
                }
            });

            LineDataSet setAngleZ = new LineDataSet(valsAngleZ, "Angle Z");
            setAngleZ.setAxisDependency(AxisDependency.LEFT);

            List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(setAngleX);
            dataSets.add(setAngleY);
            dataSets.add(setAngleZ);
            angleData = new LineData(dataSets);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        angleChart.setData(angleData);
        angleChart.invalidate(); // refresh
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        };
        XAxis xAxis = angleChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
    }

    private void rainGraph() {
        LineChartView lineChartView = (LineChartView) findViewById(R.id.graph2_rain);

        // Data
        LineSet dataset = new LineSet();
        for (int i = 0; i < weatherList.size(); i++) {
            float rain = Float.parseFloat(weatherList.get(i).getRain());

            if (rain < minRain) {
                minRain = rain;
            }

            if (rain > maxRain) {
                maxRain = rain;
            }

            dataset.addPoint(getDateLabel(weatherList.get(i), i), rain);
        }
        dataset.setSmooth(false);
        dataset.setColor(Color.parseColor("#2196F3"));
        dataset.setThickness(4);

        lineChartView.addData(dataset);

        // Grid
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(lineColor));
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        paint.setStrokeWidth(1);
        lineChartView.setGrid(ChartView.GridType.HORIZONTAL, paint);
        lineChartView.setBorderSpacing(Tools.fromDpToPx(10));
        lineChartView.setAxisBorderValues(0, (int) (Math.round(maxRain)) + 1);
        lineChartView.setStep(1);
        lineChartView.setXAxis(false);
        lineChartView.setYAxis(false);
        lineChartView.setLabelsColor(Color.parseColor(labelColor));

        lineChartView.show();
    }

    private void pressureGraph() {
        LineChartView lineChartView = (LineChartView) findViewById(R.id.graph2_pressure);

        // Data
        LineSet dataset = new LineSet();
        for (int i = 0; i < weatherList.size(); i++) {
            float pressure = UnitConvertor.convertPressure(Float.parseFloat(weatherList.get(i).getPressure()), sp);

            if (pressure < minPressure) {
                minPressure = pressure;
            }

            if (pressure > maxPressure) {
                maxPressure = pressure;
            }

            dataset.addPoint(getDateLabel(weatherList.get(i), i), pressure);
        }
        dataset.setSmooth(true);
        dataset.setColor(Color.parseColor("#4CAF50"));
        dataset.setThickness(4);

        lineChartView.addData(dataset);

        // Grid
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(lineColor));
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        paint.setStrokeWidth(1);
        lineChartView.setGrid(ChartView.GridType.HORIZONTAL, paint);
        lineChartView.setBorderSpacing(Tools.fromDpToPx(10));
        lineChartView.setAxisBorderValues((int) minPressure - 1, (int) maxPressure + 1);
        lineChartView.setStep(2);
        lineChartView.setXAxis(false);
        lineChartView.setYAxis(false);
        lineChartView.setLabelsColor(Color.parseColor(labelColor));

        lineChartView.show();
    }

    private void windSpeedGraph() {
        LineChartView lineChartView = (LineChartView) findViewById(R.id.graph2_windspeed);
        String graphLineColor = "#efd214";

        if (darkTheme) {
            graphLineColor = "#FFF600";
        }

        // Data
        LineSet dataset = new LineSet();
        for (int i = 0; i < weatherList.size(); i++) {
            float windSpeed = (float) UnitConvertor.convertWind(Float.parseFloat(weatherList.get(i).getWind()), sp);

            if (windSpeed < minWindSpeed) {
                minWindSpeed = windSpeed;
            }

            if (windSpeed > maxWindSpeed) {
                maxWindSpeed = windSpeed;
            }

            dataset.addPoint(getDateLabel(weatherList.get(i), i), windSpeed);
        }
        dataset.setSmooth(false);
        dataset.setColor(Color.parseColor(graphLineColor));
        dataset.setThickness(4);

        lineChartView.addData(dataset);

        // Grid
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor(lineColor));
        paint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        paint.setStrokeWidth(1);
        lineChartView.setGrid(ChartView.GridType.HORIZONTAL, paint);
        lineChartView.setBorderSpacing(Tools.fromDpToPx(10));
        lineChartView.setAxisBorderValues((int) minWindSpeed - 1, (int) maxWindSpeed + 1);
        lineChartView.setStep(2);
        lineChartView.setXAxis(false);
        lineChartView.setYAxis(false);
        lineChartView.setLabelsColor(Color.parseColor(labelColor));

        lineChartView.show();
    }


    public ParseResult parseLongTermJson(String result) {
        int i;
        try {
            JSONObject reader = new JSONObject(result);

            final String code = reader.optString("cod");
            if ("404".equals(code)) {
                return ParseResult.CITY_NOT_FOUND;
            }

            JSONArray list = reader.getJSONArray("list");
            for (i = 0; i < list.length(); i++) {
                Weather weather = new Weather();

                JSONObject listItem = list.getJSONObject(i);
                JSONObject main = listItem.getJSONObject("main");

                JSONObject windObj = listItem.optJSONObject("wind");
                weather.setWind(windObj.getString("speed"));

                weather.setPressure(main.getString("pressure"));
                weather.setHumidity(main.getString("humidity"));

                JSONObject rainObj = listItem.optJSONObject("rain");
                JSONObject snowObj = listItem.optJSONObject("snow");
                if (rainObj != null) {
                    weather.setRain(MainActivity.getRainString(rainObj));
                } else {
                    weather.setRain(MainActivity.getRainString(snowObj));
                }

                weather.setDate(listItem.getString("dt"));
                weather.setTemperature(main.getString("temp"));

                weatherList.add(weather);
            }
        } catch (JSONException e) {
            Log.e("JSONException Data", result);
            e.printStackTrace();
            return ParseResult.JSON_EXCEPTION;
        }

        return ParseResult.OK;
    }

    String previous = "";

    public String getDateLabel(Weather weather, int i) {
        if ((i + 4) % 4 == 0) {
            SimpleDateFormat resultFormat = new SimpleDateFormat("E");
            resultFormat.setTimeZone(TimeZone.getDefault());
            String output = resultFormat.format(weather.getDate());
            if (!output.equals(previous)) {
                previous = output;
                return output;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
    private int getTheme(String themePref) {
        switch (themePref) {
            case "dark":
                return R.style.AppTheme_NoActionBar_Dark;
            case "black":
                return R.style.AppTheme_NoActionBar_Black;
            case "classic":
                return R.style.AppTheme_NoActionBar_Classic;
            case "classicdark":
                return R.style.AppTheme_NoActionBar_Classic_Dark;
            case "classicblack":
                return R.style.AppTheme_NoActionBar_Classic_Black;
            default:
                return R.style.AppTheme_NoActionBar;
        }
    }
}
