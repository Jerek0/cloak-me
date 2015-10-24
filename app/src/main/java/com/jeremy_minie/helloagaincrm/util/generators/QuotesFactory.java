package com.jeremy_minie.helloagaincrm.util.generators;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jerek0 on 15/10/15.
 */
public class QuotesFactory {
    private static QuotesFactory ourInstance = new QuotesFactory();

    public static QuotesFactory getInstance() {
        return ourInstance;
    }

    private ArrayList<String> quotes;
    private Random randomGenerator;

    private QuotesFactory() {
        quotes = new ArrayList<String>();
        randomGenerator = new Random();

        quotes.add("Our democracy has been hacked");
        quotes.add("True courage is about being honest with yourself. Especially when it’s difficult.");
        quotes.add("Shit is actually happening, I'm talking to an imaginary person");
        quotes.add("Power belongs to the people that take it");
        quotes.add("Everyone steals, that's how it works.");
        quotes.add("I don't know what's real anymore");
        quotes.add("Your privacy has been deleted");
        quotes.add("218.108.149.373");
        quotes.add("Fun society");
    }

    public String randomQuote() {
        return quotes.get(randomGenerator.nextInt(quotes.size()));
    }
}
