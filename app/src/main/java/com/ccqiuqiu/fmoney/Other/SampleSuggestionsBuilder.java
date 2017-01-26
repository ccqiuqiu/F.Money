package com.ccqiuqiu.fmoney.Other;

import android.content.Context;

import com.ccqiuqiu.fmoney.App;
import com.ccqiuqiu.fmoney.Model.SearchHis;
import com.ccqiuqiu.fmoney.R;
import com.ccqiuqiu.fmoney.Utils.ViewUtils;

import org.cryse.widget.persistentsearch.SearchItem;
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SampleSuggestionsBuilder implements SearchSuggestionsBuilder {
    private Context mContext;
    private List<SearchItem> mHistorySuggestions = new ArrayList<SearchItem>();
    private List<SearchHis> mSearchHises;

    public SampleSuggestionsBuilder(Context context, List<SearchHis> searchHises) {
        this.mContext = context;
        mSearchHises = searchHises;
        createHistorys();
    }

    private void createHistorys() {
        if(mSearchHises != null){
            for (SearchHis searchHis : mSearchHises) {
                String query = searchHis.getName();
                SearchItem item = null;
                if(query.startsWith("@")){
                    item = new SearchItem(
                            App.mContext.getString(R.string.search_account) + ": " + query.substring(1),
                            searchHis.getName(),
                            SearchItem.TYPE_SEARCH_ITEM_HISTORY
                    );
                }else if(query.startsWith("#")){
                        item = new SearchItem(
                                App.mContext.getString(R.string.search_account) + ": " + query.substring(1),
                                searchHis.getName(),
                                SearchItem.TYPE_SEARCH_ITEM_HISTORY
                        );
                }else{
                    item = new SearchItem(
                            searchHis.getName(),
                            searchHis.getName(),
                            SearchItem.TYPE_SEARCH_ITEM_HISTORY
                    );
                }
                mHistorySuggestions.add(item);
            }
        }
    }

    @Override
    public Collection<SearchItem> buildEmptySearchSuggestion(int maxCount) {
        List<SearchItem> items = new ArrayList<SearchItem>();
        items.addAll(mHistorySuggestions);
        return items;
    }

    @Override
    public Collection<SearchItem> buildSearchSuggestion(int maxCount, String query) {
//        List<SearchItem> items = new ArrayList<SearchItem>();
//        items.addAll(mHistorySuggestions);
//        return items;
        List<SearchItem> items = new ArrayList<SearchItem>();
        if (query.startsWith("@")) {
            SearchItem peopleSuggestion = new SearchItem(
                    App.mContext.getString(R.string.search_account) + ": " + query.substring(1),
                    query,
                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
            );
            items.add(peopleSuggestion);
        } else if (query.startsWith("#")) {
            SearchItem toppicSuggestion = new SearchItem(
                    App.mContext.getString(R.string.search_tag) + ": " + query.substring(1),
                    query,
                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
            );
            items.add(toppicSuggestion);
        } else {
            SearchItem peopleSuggestion = new SearchItem(
                    App.mContext.getString(R.string.search_account) + ": " + query,
                    "@" + query,
                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
            );
            items.add(peopleSuggestion);
            SearchItem toppicSuggestion = new SearchItem(
                    App.mContext.getString(R.string.search_tag) + ": " + query,
                    "#" + query,
                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
            );
            items.add(toppicSuggestion);
        }
        for (SearchItem item : mHistorySuggestions) {
            if (item.getValue().startsWith(query)) {
                items.add(item);
            }
        }
        return items;
    }
}
