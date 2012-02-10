package openal;

import java.io.IOException;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

/**
 * A sound implementation wrapped round a player which reads (and potentially) rereads a stream.
 * This supplies streaming audio
 * 
 * @author kevin
 * @author Nathan Sweet <misc@n4te.com>
 * @author Rockstar playAsMusic cleanup
 */
public class StreamSound extends AudioImpl {
	/** The player we're going to ask to stream data */
	private OpenALStreamPlayer player;

	/**
	 * Create a new sound wrapped round a stream
	 * 
	 * @param player
	 *            The stream player we'll use to access the stream
	 */
	public StreamSound(OpenALStreamPlayer player) {
		this.player = player;
	}

	/**
	 * @see openal.newdawn.slick.openal.AudioImpl#isPlaying()
	 */
	@Override
	public boolean isPlaying() {
		return SoundStore.get().isPlaying(player);
	}

	/**
	 * @see openal.newdawn.slick.openal.AudioImpl#playAsMusic(float, float, boolean)
	 */
	@Override
	public int playAsMusic(float pitch, float gain, boolean loop) {
		try {
			cleanUpSource();

			player.setup(pitch);
			player.play(loop);
			SoundStore.get().setStream(player);
		} catch (IOException e) {

		}

		return SoundStore.get().getSource(0);
	}

	/**
	 * Clean up the buffers applied to the sound source
	 */
	private void cleanUpSource() {
		SoundStore store = SoundStore.get();

		AL10.alSourceStop(store.getSource(0));
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		int queued = AL10.alGetSourcei(store.getSource(0), AL10.AL_BUFFERS_QUEUED);

		while (queued > 0)
		{
			AL10.alSourceUnqueueBuffers(store.getSource(0), buffer);
			queued--;
		}

		AL10.alSourcei(store.getSource(0), AL10.AL_BUFFER, 0);
	}

	/**
	 * @see openal.newdawn.slick.openal.AudioImpl#playAsSoundEffect(float, float, boolean, float,
	 *      float, float)
	 */
	@Override
	public int playAsSoundEffect(float pitch, float gain, boolean loop, float x, float y, float z) {
		return playAsMusic(pitch, gain, loop);
	}

	/**
	 * @see openal.newdawn.slick.openal.AudioImpl#playAsSoundEffect(float, float, boolean)
	 */
	@Override
	public int playAsSoundEffect(float pitch, float gain, boolean loop) {
		return playAsMusic(pitch, gain, loop);
	}

	/**
	 * @see openal.newdawn.slick.openal.AudioImpl#stop()
	 */
	@Override
	public void stop() {
		SoundStore.get().setStream(null);
	}

	/**
	 * @see openal.newdawn.slick.openal.AudioImpl#setPosition(float)
	 */
	@Override
	public boolean setPosition(float position) {
		return player.setPosition(position);
	}

	/**
	 * @see openal.newdawn.slick.openal.AudioImpl#getPosition()
	 */
	@Override
	public float getPosition() {
		return player.getPosition();
	}
}
