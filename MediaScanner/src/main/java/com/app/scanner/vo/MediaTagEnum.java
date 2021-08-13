package com.app.scanner.vo;

public enum MediaTagEnum {
    //字母
    TAG_LETTER("usb1"),

    //专辑
    TAG_ALBUM ("usb1"),

    //歌手
    TAG_SINGER ("usb1");

    private String name;


    private MediaTagEnum(String name) {
        this.name = name;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
