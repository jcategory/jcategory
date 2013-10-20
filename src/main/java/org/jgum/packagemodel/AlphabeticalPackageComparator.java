package org.jgum.packagemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlphabeticalPackageComparator implements Comparator<PackageNode> {

//	public static List<PackageNode> orderAlphabetically(List<PackageNode> unorderedNodes) {
//		List<PackageNode> orderedNodes = new ArrayList<>(unorderedNodes);
//		Collections.sort(orderedNodes, new AlphabeticalPackageComparator());
//		return orderedNodes;
//	}
	
	@Override
	public int compare(PackageNode pn1, PackageNode pn2) {
		return pn1.getPackageFragment().compareTo(pn2.getPackageFragment());
	}

}
