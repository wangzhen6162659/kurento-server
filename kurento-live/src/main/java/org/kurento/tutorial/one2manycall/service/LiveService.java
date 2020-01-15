package org.kurento.tutorial.one2manycall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.kurento.tutorial.one2manycall.dto.UserResDTO;
import org.kurento.tutorial.one2manycall.entity.Live;
import org.kurento.tutorial.one2manycall.entity.User;

/**
 *
 * Live 表数据服务层接口实现类
 *
 */

public interface LiveService extends IService<Live> {
  Page<Live> selectLivePage(Page<Live> page, Boolean state);

  Live getByUser(Long userId);
}
