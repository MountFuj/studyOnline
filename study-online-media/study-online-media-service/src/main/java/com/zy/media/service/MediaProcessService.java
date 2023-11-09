package com.zy.media.service;

import com.zy.media.model.po.MediaProcess;


import java.util.List;


public interface MediaProcessService {
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);
    public boolean startTask(long id);
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}

