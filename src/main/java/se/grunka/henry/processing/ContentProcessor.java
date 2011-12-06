package se.grunka.henry.processing;

public interface ContentProcessor {
	boolean shouldProcess(String name);
	Content process(String name, Content content);
}
