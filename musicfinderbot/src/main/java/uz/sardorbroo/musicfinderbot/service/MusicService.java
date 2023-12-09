package uz.sardorbroo.musicfinderbot.service;

import uz.sardorbroo.musicfinderbot.service.dto.MusicDTO;
import uz.sardorbroo.musicfinderbot.service.dto.PageDTO;

import java.util.List;

public interface MusicService {

    List<MusicDTO> find(String musicName, PageDTO pagination);

    byte[] download(String musicId);
}
