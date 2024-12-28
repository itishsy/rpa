package com.seebon.rpa.service.impl;

import com.seebon.rpa.entity.auth.po.SysResource;
import com.seebon.rpa.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Override
    public List<SysResource> findByUser(String username) {
        return null;
    }

    @Override
    public List<SysResource> findByRole(String role) {
        return null;
    }

    @Override
    public List<SysResource> findByClient(String clientId) {
        return null;
    }
}
