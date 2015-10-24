package com.jeremy_minie.helloagaincrm.util.generators;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jerek0 on 15/10/15.
 */
public class UsernameGenerator {
    private static UsernameGenerator ourInstance = new UsernameGenerator();

    private ArrayList<String> adjectives;
    private ArrayList<String> nouns;
    private Random randomGenerator;

    public static UsernameGenerator getInstance() {
        return ourInstance;
    }

    private UsernameGenerator() {
        adjectives = new ArrayList<String>();
        nouns = new ArrayList<String>();

        adjectives.add("Incandescent");
        adjectives.add("Funny");
        adjectives.add("Beautiful");
        adjectives.add("Arrogant");
        adjectives.add("Marvelous");
        adjectives.add("Ugly");
        adjectives.add("Corpulent");
        adjectives.add("Feckless");
        adjectives.add("Loquacious");
        adjectives.add("Salubrious");
        adjectives.add("Turbulent");
        adjectives.add("Crapulous");
        adjectives.add("Antic");
        adjectives.add("Legendary");

        nouns.add("Turtle");
        nouns.add("Hacker");
        nouns.add("Quokka");
        nouns.add("Goat");
        nouns.add("Velociraptor");
        nouns.add("Brachiosaurus");
        nouns.add("Lemur");
        nouns.add("Monkey");
        nouns.add("Sloth");
        nouns.add("Elephant");
        nouns.add("Fox");
        nouns.add("Pug");
        nouns.add("Cat");
        nouns.add("Hummingbird");
        nouns.add("Parrot");

        randomGenerator = new Random();
    }

    public String newUsername() {
        String adjective = (String) adjectives.get(randomGenerator.nextInt(adjectives.size()));
        String noun = (String) nouns.get(randomGenerator.nextInt(nouns.size()));
        int number = (int) Math.floor(Math.random()*1000);

        return adjective+"-"+noun+"-"+number;
    }
}
