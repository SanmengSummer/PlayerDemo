package com.app.scanner.device;

public enum DeviceTypeEnum {
    USB_1("usb1", 0,0),
    USB_2("usb2", 1,1),
    CAR_PLAY("carPlay", 2),
    CAR_LIFE("carLife", 3),
    BLUE_TOOTH("blueTooth", 4);

    private String name;
    private Integer index;

    private Integer vendorId;

    private DeviceTypeEnum(String name, Integer index) {
        this.name = name;
        this.index = index;
    }

    private DeviceTypeEnum(String name, Integer index, int vendorId) {
        this.name = name;
        this.index = index;
        this.vendorId = vendorId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
