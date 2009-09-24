/*
 * Created on 2006.8.4
 */

package com.springrts.tasserver;

/**
 * @author Betalord
 */
public class GlobalMapGrade {

	public String mapHash;
	public float avGrade = 0;
	public int noVotes = 0;

	public GlobalMapGrade(String mapHash) {
		this.mapHash = mapHash;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (!(obj instanceof GlobalMapGrade)) return false;
		return this.mapHash.equals(((GlobalMapGrade)obj).mapHash);
	}
}
