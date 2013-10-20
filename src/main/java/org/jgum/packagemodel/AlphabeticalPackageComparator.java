package org.jgum.packagemodel;

import java.util.Comparator;

public class AlphabeticalPackageComparator implements Comparator<PackageNode> {
	
	@Override
	public int compare(PackageNode pn1, PackageNode pn2) {
		return pn1.getPackageFragment().compareTo(pn2.getPackageFragment());
	}

}
