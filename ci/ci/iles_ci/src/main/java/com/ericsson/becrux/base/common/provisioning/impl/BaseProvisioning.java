package com.ericsson.becrux.base.common.provisioning.impl;

import com.ericsson.becrux.base.common.provisioning.Provisioning;
import com.ericsson.becrux.base.common.provisioning.ProvisioningProperties;

/**
 * Base Implementation of {@link Provisioning}
 */
public abstract class BaseProvisioning implements Provisioning {

    private ProvisioningProperties properties;

    /**
     * Constructor.
     * @return
     */
    public BaseProvisioning(ProvisioningProperties properties) {
        this.properties = properties;
    }

    /**
     * Getter.
     * @return
     */
    public ProvisioningProperties getProperties() {
        return properties;
    }

    /**
     * Setter.
     * @param properties
     */
    public void setProperties(ProvisioningProperties properties) {
        this.properties = properties;
    }

}
