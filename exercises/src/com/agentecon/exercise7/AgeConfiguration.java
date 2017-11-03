/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise7;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.agentecon.configuration.StocksConfiguration;

public class AgeConfiguration extends StocksConfiguration {

	public AgeConfiguration() throws SocketTimeoutException, IOException {
		super(500);
	}

}
