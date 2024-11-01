package com.ctunite.flyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerView;

public class MainActivity extends AppCompatActivity {
    private NodePlayerView nodePlayerView;
    private NodePlayer nodePlayer;
    private Button playVideo;
    private EditText rtspAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playVideo = (Button) findViewById(R.id.play_vedio);
        rtspAddress = (EditText) findViewById(R.id.rtsp_address);

        nodePlayerView = findViewById(R.id.node_player_view);
        nodePlayerView.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
        nodePlayerView.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFill);
        nodePlayer = new NodePlayer(this);
        nodePlayer.setPlayerView(nodePlayerView);
        nodePlayer.setRtspTransport(NodePlayer.RTSP_TRANSPORT_TCP);

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rtspAddressString = rtspAddress.getText().toString();

                nodePlayer.setInputUrl(rtspAddressString);
                nodePlayer.setVideoEnable(true);
                nodePlayer.start();
            }
        });



    }
}