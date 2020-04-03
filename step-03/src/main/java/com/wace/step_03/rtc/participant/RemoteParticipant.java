package com.wace.step_03.rtc.participant;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wace.step_03.R;

import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoTrack;

/**
 * author：lwf
 * date：2020/4/2
 * description：
 */
public class RemoteParticipant extends Participant {

	private View view;
	private SurfaceViewRenderer videoRenderer;
	private TextView participantNameText;

	public RemoteParticipant(String connectionId, String participantName, Context context) {
		super(connectionId, participantName, context);
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public SurfaceViewRenderer getVideoRenderer() {
		return videoRenderer;
	}

	public void setVideoRenderer(SurfaceViewRenderer videoRenderer) {
		this.videoRenderer = videoRenderer;
	}

	public TextView getParticipantNameText() {
		return participantNameText;
	}

	public void setParticipantNameText(TextView participantNameText) {
		this.participantNameText = participantNameText;
	}

	public void createRemoteParticipantVideo(ViewGroup views_container, Activity context, RemoteParticipant remoteParticipant) {
		View rowView = context.getLayoutInflater().inflate(R.layout.peer_video, null);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 0, 20);
		rowView.setLayoutParams(lp);
		int rowId = View.generateViewId();
		rowView.setId(rowId);
		views_container.addView(rowView);
		SurfaceViewRenderer videoView = (SurfaceViewRenderer) ((ViewGroup) rowView).getChildAt(0);
		remoteParticipant.setVideoRenderer(videoView);
		videoView.setMirror(false);
		EglBase rootEglBase = EglBase.create();
		videoView.init(rootEglBase.getEglBaseContext(), null);
		videoView.setZOrderMediaOverlay(true);
		View textView = ((ViewGroup) rowView).getChildAt(1);
		remoteParticipant.setParticipantNameText((TextView) textView);
		remoteParticipant.setView(rowView);

		remoteParticipant.getParticipantNameText().setText(remoteParticipant.getParticipantName());
		remoteParticipant.getParticipantNameText().setPadding(20, 3, 20, 3);
	}

	public void setRemoteMediaStream(MediaStream stream, RemoteParticipant remoteParticipant) {
		VideoTrack videoTrack = stream.videoTracks.get(0);
		videoTrack.addSink(remoteParticipant.getVideoRenderer());
	}
}
