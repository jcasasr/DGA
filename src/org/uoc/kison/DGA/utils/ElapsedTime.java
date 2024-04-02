/*
 * Copyright 2015 Jordi Casas-Roma, Alexandre Dotor Casals
 * 
 * This file is part of DGA. 
 * 
 * DGA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DGA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DGA.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.uoc.kison.DGA.utils;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author jcasasr
 */
public class ElapsedTime {
    private long timeIni;
    private long timeElapsed;
    
    public ElapsedTime() {
        this.timeIni = System.currentTimeMillis();
    }
    
    public void stop() {
        this.timeElapsed = System.currentTimeMillis() - this.timeIni;
    }
    
    public String getElapsedTime() {
        long l = timeElapsed;
        
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));

        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }
}
