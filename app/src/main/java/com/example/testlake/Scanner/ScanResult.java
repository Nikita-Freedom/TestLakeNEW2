package com.example.testlake.Scanner;

import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

// Final data class holding basic information about a scanned barcode.
public final class ScanResult implements Serializable {

    public Symbology getSymbology() {
        return symbology;
    }

    public String getData() {
        return data;
    }

    public final Symbology symbology;
    public final String data;
    public ScanResult(Barcode barcode) {
        symbology = barcode.getSymbology();
        data = barcode.getData() != null ? barcode.getData() : "";
    }

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbology, data);
    }
}