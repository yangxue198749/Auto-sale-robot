package com.kdx.vms.service;

import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtVmThemeEntrance;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 2017/5/3.
 */
public interface IMtThemeService {

    List getThemesByName(TSysUser currUser, String themeName, Integer pageSize);

    SystemCode deleteThemes(TSysUser currUser, List<Integer> themeIds);

    SystemCode createTheme(TSysUser currUser, String themeName, String themeDesc, String themeUrl, String themePreview, Long size, String md5);

    SystemCode bindTheme(TSysUser currUser, Integer[] vmIds, Integer themeId);

    SystemCode setFormat(TSysUser currUser, Integer[] vmIds, Integer formatCode);

    SystemCode setConfiguration(ObjectContext context, Integer resourceId, Integer vmId, String fileName, Integer index);

    SystemCode delectConfiguration(ObjectContext context, Integer vmId, Integer index);

    TMtVmThemeEntrance findByVmId(ObjectContext context, Integer index, Integer vmId);
}
