# Supported formats

MPGO relies on native [MediaPlayer API](https://developer.android.com/guide/topics/media/mediaplayer) for audio files reproduction.

### List of [supported file types](https://developer.android.com/guide/topics/media/media-formats)

- AAC LC
- HE-AACv1 (AAC+)
- HE-AACv2 (enhanced AAC+)
- AAC ELD (enhanced low delay AAC)
- AMR-NB
- AMR-WB
- FLAC
- GSM
- MIDI
- MP3
- Opus
- PCM/WAVE
- Vorbis

### Instructions to get certain codecs working

Opus, for example, is supported natively since Android 5.0, but only in the Matroska (.mkv) container. Opus file type/container is not detected and not supported by MediaPlayer API. A simple workaround to detect and play Opus encoded files is to **replace their extension** (.opus) with the supported container (.mkv). Check the "Supported File Type(s) / Container Formats" from the [supported media formats](https://developer.android.com/guide/topics/media/media-formats) page to know the supported container given the format/codec.

**Thanks for using Music Player GO**