//
// Created by 高成佳 on 2021/7/30.
//

#ifndef SCANNER_MEDIASTATUS_H
#define SCANNER_MEDIASTATUS_H

enum {
    STATUS_UNKNOW       = -1,
    STATUS_OK           = 0,
    STATUS_FINISHED     = 100,
    STATUS_START        = 100 - 1,
    STATUS_SCANNING     = 100 - 2,
    STATUS_STOP         = 100 - 3,
    STATUS_BUSY         = 100 - 4,
    STATUS_EMPTY        = 100 - 5,
    STATUS_INVALID      = 100 - 6,
};

enum {
    EVT_SCAN_START = 0,
    EVT_SCAN_SCANNING,
    EVT_SCAN_EMPTY,
    EVT_SCAN_FINISHED,
};

#endif //SCANNER_MEDIASTATUS_H
