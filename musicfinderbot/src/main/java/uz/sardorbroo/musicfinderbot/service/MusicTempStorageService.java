package uz.sardorbroo.musicfinderbot.service;

import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;

import java.util.Optional;
import java.util.UUID;

public interface MusicTempStorageService {

    Optional<UUID> save(MusicDTO music);

    Optional<MusicDTO> getById(UUID musicId);

}
