package com.zensleep;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FavFragment extends Fragment {

    public FavFragment() {
        super(R.layout.fragment_fav);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        TextView favList = view.findViewById(R.id.favList);

        StringBuilder builder = new StringBuilder();

        if (FavoritesManager.isFavorite(getContext(), "chuva")) {
            builder.append("🌧 Chuva\n");
        }

        if (FavoritesManager.isFavorite(getContext(), "mar")) {
            builder.append("🌊 Ondas do Mar\n");
        }

        if (builder.length() == 0) {
            builder.append("Nenhum favorito ainda.");
        }

        favList.setText(builder.toString());
    }
}
