//package com.daon.be.tts.application;
//
//import com.daon.be.tts.domain.model.*;
//import com.daon.be.tts.domain.port.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.OutputStream;
//
//@Service
//@RequiredArgsConstructor
//public class ReadService {
//
//    private final ReadingTextRepository readingTextRepository;
//    private final TtsClient ttsClient;
//    private final PronunciationEvalClient evalClient;
//
//    @org.springframework.transaction.annotation.Transactional(readOnly = true)
//    public void streamByTextId(Long textId, Voice voice, AudioFormat format, Speed speed,
//                               OutputStream clientOut, boolean mirrorToEval) {
//        String text = readingTextRepository.findTextById(textId)
//                .orElseThrow(() -> new IllegalArgumentException("텍스트 없음: " + textId));
//
//        TtsCommand cmd = TtsCommand.of(text, voice, format, speed);
//
//        java.io.ByteArrayOutputStream mirror = mirrorToEval ? new java.io.ByteArrayOutputStream() : null;
//        OutputStream out = (mirror == null) ? clientOut : new DualOutputStream(clientOut, mirror);
//
//        ttsClient.stream(cmd, out);
//
//        if (mirror != null && format == AudioFormat.WAV) {
//            evalClient.evaluate(mirror.toByteArray(), text);
//        }
//    }
//
//    /** 두 스트림 동시 쓰기: flush/close도 위임 */
//    static class DualOutputStream extends OutputStream {
//        private final OutputStream a, b;
//        DualOutputStream(OutputStream a, OutputStream b) { this.a = a; this.b = b; }
//        @Override public void write(int b1) throws java.io.IOException { a.write(b1); b.write(b1); }
//        @Override public void write(byte[] buf, int off, int len) throws java.io.IOException {
//            a.write(buf, off, len); b.write(buf, off, len);
//        }
//        @Override public void flush() throws java.io.IOException { a.flush(); b.flush(); }
//        @Override public void close() throws java.io.IOException { a.close(); b.close(); }
//    }
//}
