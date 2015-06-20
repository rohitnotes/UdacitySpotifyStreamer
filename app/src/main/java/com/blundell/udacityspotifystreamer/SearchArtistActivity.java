package com.blundell.udacityspotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.novoda.notils.logger.simple.Log;
import com.novoda.notils.logger.toast.ToastDisplayer;
import com.novoda.notils.logger.toast.ToastDisplayers;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.client.Response;

public class SearchArtistActivity extends AppCompatActivity {

    private ArtistsAdapter artistsAdapter;
    private ToastDisplayer toaster;
    private SpotifyService spotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);
        toaster = ToastDisplayers.noPendingToastsToastDisplayer(this);
        SpotifyApi spotifyApi = new SpotifyApiBuilder().executeOnBackgroundThread().callbackOnMainThread().create();
        spotifyService = spotifyApi.getService();

        RecyclerView artistResultsList = (RecyclerView) findViewById(R.id.search_artist_list_results);
        artistResultsList.setHasFixedSize(true);
        artistResultsList.setLayoutManager(new LinearLayoutManager(this));
        artistsAdapter = new ArtistsAdapter();
        artistResultsList.setAdapter(artistsAdapter);
        EditText searchArtistInputBox = (EditText) findViewById(R.id.search_artist_box_input);
        searchArtistInputBox.addTextChangedListener(onChangedQueryApi);
    }

    private final OnChangedTextWatcher onChangedQueryApi = new OnChangedTextWatcher() {
        @Override
        void onTextChanged(String text) {
            if (text.isEmpty()) {
                return;
            }
            spotifyService.searchArtists(text, onArtistsFoundUpdateView);
        }
    };
    private final SpotifyCallback<ArtistsPager> onArtistsFoundUpdateView = new SpotifyCallback<ArtistsPager>() {
        @Override
        public void success(ArtistsPager artistsPager, Response response) {
            if (artistsPager.artists.items.isEmpty()) {
                popToast(R.string.error_no_results);
            }
            artistsAdapter.setArtists(artistsPager.artists.items);
        }

        @Override
        public void failure(SpotifyError spotifyError) {
            Log.e(spotifyError);
            popToast(R.string.fubar_error_message);
        }
    };

    private void popToast(int message) {
        toaster.display(message);
    }
}
