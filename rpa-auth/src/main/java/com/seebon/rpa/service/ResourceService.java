package com.seebon.rpa.service;

import java.util.List;

import com.seebon.rpa.entity.auth.po.SysResource;

public interface ResourceService {

    List<SysResource> findByUser(String username);

    List<SysResource> findByRole(String role);

    List<SysResource> findByClient(String clientId);
}