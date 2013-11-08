package org.jgum.testutil;


public class AnimalHierarchy {

	public static class Animal {}
	public static interface HasLegs {}
	public static interface FourLegged extends HasLegs {}
	public static interface Furry {}
	public static class Cat extends Animal implements Furry, FourLegged {}
	public static class Fish extends Animal {}
	
}
