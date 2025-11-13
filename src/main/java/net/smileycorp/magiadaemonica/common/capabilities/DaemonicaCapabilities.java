package net.smileycorp.magiadaemonica.common.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class DaemonicaCapabilities {

	@CapabilityInject(ISoul.class)
	public final static Capability<ISoul> SOUL = null;
	
}
