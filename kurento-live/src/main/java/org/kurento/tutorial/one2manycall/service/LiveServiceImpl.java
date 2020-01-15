package org.kurento.tutorial.one2manycall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.kurento.tutorial.one2manycall.common.HyLambdaQueryWrapper;
import org.kurento.tutorial.one2manycall.dao.LiveMapper;
import org.kurento.tutorial.one2manycall.entity.Live;
import org.kurento.tutorial.one2manycall.entity.User;
import org.springframework.stereotype.Service;

/**
 *
 * Live 表数据服务层接口实现类
 *
 */
@Service
public class LiveServiceImpl extends ServiceImpl<LiveMapper, Live> implements LiveService {
  public Page<Live> selectLivePage(Page<Live> page, Boolean state) {
    page.setRecords(baseMapper.selectLiveList(page,state));
    return page;
  }

  @Override
  public Live getByUser(Long userId) {
    HyLambdaQueryWrapper<Live> wrapper = HyLambdaQueryWrapper.lambdaQuery();
    wrapper.eq(Live::getCreateUser, userId);
    return getOne(wrapper, false);
  }
}
