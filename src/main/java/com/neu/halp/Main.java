package com.neu.halp;

import java.io.*;

import com.google.gson.JsonStreamParser;
import com.google.gson.Gson;

import com.neu.halp.client.ClientEntry;
import com.neu.halp.data.Configuration;
import com.neu.halp.data.Seniors;

public class Main {
    public static void main(String[] args) {
        try {
            execute(new FileReader(args[0]), new PrintWriter(System.out));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    public static void execute(final Reader r, final Writer w) {
        final Gson gson = new Gson();
        final JsonStreamParser jsr = new JsonStreamParser(r);

        if (jsr.hasNext()) {
            final Configuration config = Configuration.fromJsonElement(jsr.next());
            final Seniors outputSeniors = config.computeSeniors();
            String output = gson.toJson(outputSeniors);
            try {
                w.write(output);
                w.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                w.write("No JSON available on reader.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}