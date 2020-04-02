package com.wace.step_03.rtc;

import org.webrtc.AudioTrack;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public abstract class Participant {

	protected List<IceCandidate> candidateList = new ArrayList<>();
	protected PeerConnection peerConnection;
	protected AudioTrack audioTrack;
	protected VideoTrack videoTrack;
	protected MediaStream mediaStream;

	public List<IceCandidate> getCandidateList() {
		return candidateList;
	}

	public void setCandidateList(List<IceCandidate> candidateList) {
		this.candidateList = candidateList;
	}

	public PeerConnection getPeerConnection() {
		return peerConnection;
	}

	public void setPeerConnection(PeerConnection peerConnection) {
		this.peerConnection = peerConnection;
	}

	public AudioTrack getAudioTrack() {
		return audioTrack;
	}

	public void setAudioTrack(AudioTrack audioTrack) {
		this.audioTrack = audioTrack;
	}

	public VideoTrack getVideoTrack() {
		return videoTrack;
	}

	public void setVideoTrack(VideoTrack videoTrack) {
		this.videoTrack = videoTrack;
	}

	public MediaStream getMediaStream() {
		return mediaStream;
	}

	public void setMediaStream(MediaStream mediaStream) {
		this.mediaStream = mediaStream;
	}

	public void dispost() {
		if (peerConnection != null) {
			peerConnection.close();
		}
	}

}
