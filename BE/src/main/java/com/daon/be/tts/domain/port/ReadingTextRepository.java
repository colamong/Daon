package com.daon.be.tts.domain.port;

import java.util.Optional;

public interface ReadingTextRepository{

    Optional<String> findTextById(Long id);
}
