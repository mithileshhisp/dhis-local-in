package org.hisp.dhis.coldchain.equipment;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.common.GenericStore;

public interface EquipmentStatusStore extends GenericStore<EquipmentStatus>
{
    String ID = EquipmentStatusStore.class.getName();
    
    //int addEquipmentStatus( EquipmentStatus equipmentStatus );

    //void updateEquipmentStatus( EquipmentStatus equipmentStatus );

    //void deleteEquipmentStatus( EquipmentStatus equipmentStatus );

    //Collection<EquipmentStatus> getAllEquipmentStatus();
    
    Collection<EquipmentStatus> getEquipmentStatusHistory( Equipment equipment );
    
    Collection<EquipmentStatus> getEquipmentStatusHistoryDescOrder( Equipment equipment );
    
    Collection<EquipmentStatus> getEquipmentStatusHistoryByTypeDescOrder( Equipment equipment, String type );
    
    EquipmentStatus getEquipmentStatus( Equipment equipment, Date reportDate );
    
    EquipmentStatus getEquipmentStatusByReportingDate( Equipment equipment, Date reportDate );
}
