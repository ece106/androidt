package com.thinqtv.thinqtv_android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.thinqtv.thinqtv_android.data.model.AboutUsModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link aboutus_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class aboutus_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
    final String strMessage = "https://en.wikipedia.org/";


    // TODO: Rename and change types of parameters
    private AboutUsModel aboutUsModel;

    public aboutus_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment aboutus_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static aboutus_fragment newInstance(String param1, String param2) {
        aboutus_fragment fragment = new aboutus_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.aboutus_fragment, container, false);
        ((RelativeLayout) view.findViewById(R.id.rlAboutUs)).setVisibility(View.GONE);

        new WebElementsTask().execute();
        return view;
    }

    private void setAboutUsModel(String sTitle1, String sContent1, String sTitle2, String sContent2, String sTitle3, String sContent3){
        aboutUsModel = new AboutUsModel();

        aboutUsModel.Title = getContext().getString(R.string.about_us);
        aboutUsModel.DetailsSection1Title = sTitle1;
        aboutUsModel.DetailsSection1Content = sContent1;

        aboutUsModel.DetailsSection2Title = sTitle2;
        aboutUsModel.DetailsSection2Content = sContent2;

        aboutUsModel.DetailsSection3Title = sTitle3;
        aboutUsModel.DetailsSection3Content = sContent3;

        TextView title = view.findViewById(R.id.about_us_title);
        TextView section1Title = view.findViewById(R.id.text_view_details1_title);
        TextView section1Content = view.findViewById(R.id.text_view_details1_content);
        TextView section2Title = view.findViewById(R.id.text_view_details2_title);
        TextView section2Content = view.findViewById(R.id.text_view_details2_content);
        TextView section3Title = view.findViewById(R.id.text_view_details3_title);
        TextView section3Content = view.findViewById(R.id.text_view_details3_content);

        if (sTitle1.length() == 0 || sContent1.length() == 0 || sTitle2.length() == 0 || sContent2.length() == 0 || sContent3.length() == 0 || sTitle3.length() == 0){
            ((RelativeLayout)view.findViewById(R.id.rl_Error)).setVisibility(View.VISIBLE);
        } else {
            title.setText(aboutUsModel.Title);
            section1Title.setText(aboutUsModel.DetailsSection1Title);
            section1Content.setText("\t" + aboutUsModel.DetailsSection1Content.replace("amp;", ""));
            section2Title.setText(aboutUsModel.DetailsSection2Title);
            section2Content.setText("\t" + aboutUsModel.DetailsSection2Content.replace("amp;", ""));
            section3Title.setText(aboutUsModel.DetailsSection3Title);
            section3Content.setText("\t" + aboutUsModel.DetailsSection3Content.replace("amp;", ""));
            ((RelativeLayout) view.findViewById(R.id.rlAboutUs)).setVisibility(View.VISIBLE);
        }
    }

    protected void maintenanceMessage() {
        view.findViewById(R.id.rl_Error).setVisibility(View.VISIBLE);
    }

    private class WebElementsTask extends AsyncTask<Void, Void, Void> {
        String sectionTitle1;
        String sectionContent1;

        String sectionTitle2;
        String sectionContent2;

        String sectionTitle3;
        String sectionContent3;

        Boolean success = false;

        ArrayList<Element> elements = new ArrayList<>();
        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            try{
                Document doc = Jsoup.connect(getContext().getString(R.string.about_us_url)).get();

                //Get Title values
                Element title1 = doc.getElementById(getContext().getString(R.string.app_title_1));
                Element title2 = doc.getElementById(getContext().getString(R.string.app_title_2));
                Element title3 = doc.getElementById(getContext().getString(R.string.app_title_3));

                sectionTitle1 = parseTag(title1.toString());
                sectionTitle2 = parseTag(title2.toString());
                sectionTitle3 = parseTag(title3.toString());

                Element content1 = doc.getElementById(getContext().getString(R.string.app_content_1));
                Element content2 = doc.getElementById(getContext().getString(R.string.app_content_2));
                Element content3 = doc.getElementById(getContext().getString(R.string.app_content_3));

                sectionContent1 = parseTag(content1.toString());
                sectionContent2 = parseTag(content2.toString());
                //sectionContent3 = parseTag(content3.toString());

                // TODO: We need to use appContent3 when the html gets corrected.
                Elements elementContent2 = doc.getElementsByClass("black");
                sectionContent3 = parseTag(elementContent2.get(1).toString());

                success = true;
                Log.i("ABOUT_US", "elements.size() = " + elements.size());
            } catch (Exception e) {
                Log.e("ABOUT_US", "FAILED");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (success) {
                setAboutUsModel(sectionTitle1, sectionContent1, sectionTitle2, sectionContent2, sectionTitle3, sectionContent3);
            } else {
                maintenanceMessage();
            }
        }

        private String parseTag(String tag) {
            String[] arrOfStr = tag.split(">",2);
            String[] arrResult = arrOfStr[1].split("<",2);
            return arrResult[0];
        }
    }
}
