package com.google.gwt.i18n.client;

import com.google.gwt.i18n.client.impl.CurrencyDataImpl;
import com.google.gwt.core.client.JavaScriptObject;
import java.util.HashMap;

public class CurrencyList_ extends com.google.gwt.i18n.client.CurrencyList {
  
  @Override
  protected CurrencyData getDefaultJava() {
    return new CurrencyDataImpl("USD", "US$", 2, "US$");
  }
  
  @Override
  protected native CurrencyData getDefaultNative() /*-{
    return [ "USD", "US$", 2, "US$"];
  }-*/;
  
  @Override
  protected HashMap<String, CurrencyData> loadCurrencyMapJava() {
    HashMap<String, CurrencyData> result = super.loadCurrencyMapJava();
    // ADP
    result.put("ADP", new CurrencyDataImpl("ADP", "ADP", 128));
    // AED
    result.put("AED", new CurrencyDataImpl("AED", "DH", 2, "DH"));
    // AFA
    result.put("AFA", new CurrencyDataImpl("AFA", "AFA", 130));
    // AFN
    result.put("AFN", new CurrencyDataImpl("AFN", "Af", 0));
    // ALL
    result.put("ALL", new CurrencyDataImpl("ALL", "ALL", 0));
    // AMD
    result.put("AMD", new CurrencyDataImpl("AMD", "AMD", 0));
    // ANG
    result.put("ANG", new CurrencyDataImpl("ANG", "NAf.", 2));
    // AOA
    result.put("AOA", new CurrencyDataImpl("AOA", "Kz", 2));
    // AOK
    result.put("AOK", new CurrencyDataImpl("AOK", "AOK", 130));
    // AON
    result.put("AON", new CurrencyDataImpl("AON", "AON", 130));
    // AOR
    result.put("AOR", new CurrencyDataImpl("AOR", "AOR", 130));
    // ARA
    result.put("ARA", new CurrencyDataImpl("ARA", "₳", 130));
    // ARL
    result.put("ARL", new CurrencyDataImpl("ARL", "$L", 130));
    // ARM
    result.put("ARM", new CurrencyDataImpl("ARM", "m$n", 130));
    // ARP
    result.put("ARP", new CurrencyDataImpl("ARP", "ARP", 130));
    // ARS
    result.put("ARS", new CurrencyDataImpl("ARS", "AR$", 2, "AR$"));
    // ATS
    result.put("ATS", new CurrencyDataImpl("ATS", "ATS", 130));
    // AUD
    result.put("AUD", new CurrencyDataImpl("AUD", "AU$", 2, "AU$"));
    // AWG
    result.put("AWG", new CurrencyDataImpl("AWG", "Afl.", 2));
    // AZM
    result.put("AZM", new CurrencyDataImpl("AZM", "AZM", 130));
    // AZN
    result.put("AZN", new CurrencyDataImpl("AZN", "man.", 2));
    // BAD
    result.put("BAD", new CurrencyDataImpl("BAD", "BAD", 130));
    // BAM
    result.put("BAM", new CurrencyDataImpl("BAM", "KM", 2));
    // BBD
    result.put("BBD", new CurrencyDataImpl("BBD", "Bds$", 2));
    // BDT
    result.put("BDT", new CurrencyDataImpl("BDT", "Tk", 2, "Tk"));
    // BEC
    result.put("BEC", new CurrencyDataImpl("BEC", "BEC", 2));
    // BEF
    result.put("BEF", new CurrencyDataImpl("BEF", "BF", 130));
    // BEL
    result.put("BEL", new CurrencyDataImpl("BEL", "BEL", 2));
    // BGL
    result.put("BGL", new CurrencyDataImpl("BGL", "BGL", 130));
    // BGN
    result.put("BGN", new CurrencyDataImpl("BGN", "BGN", 2));
    // BHD
    result.put("BHD", new CurrencyDataImpl("BHD", "BD", 3));
    // BIF
    result.put("BIF", new CurrencyDataImpl("BIF", "FBu", 0));
    // BMD
    result.put("BMD", new CurrencyDataImpl("BMD", "BD$", 2));
    // BND
    result.put("BND", new CurrencyDataImpl("BND", "BN$", 2));
    // BOB
    result.put("BOB", new CurrencyDataImpl("BOB", "Bs", 2));
    // BOP
    result.put("BOP", new CurrencyDataImpl("BOP", "$b.", 130));
    // BOV
    result.put("BOV", new CurrencyDataImpl("BOV", "BOV", 2));
    // BRB
    result.put("BRB", new CurrencyDataImpl("BRB", "BRB", 130));
    // BRC
    result.put("BRC", new CurrencyDataImpl("BRC", "BRC", 130));
    // BRE
    result.put("BRE", new CurrencyDataImpl("BRE", "BRE", 130));
    // BRL
    result.put("BRL", new CurrencyDataImpl("BRL", "R$", 2, "R$"));
    // BRN
    result.put("BRN", new CurrencyDataImpl("BRN", "BRN", 130));
    // BRR
    result.put("BRR", new CurrencyDataImpl("BRR", "BRR", 130));
    // BSD
    result.put("BSD", new CurrencyDataImpl("BSD", "BS$", 2));
    // BTN
    result.put("BTN", new CurrencyDataImpl("BTN", "Nu.", 2));
    // BUK
    result.put("BUK", new CurrencyDataImpl("BUK", "BUK", 130));
    // BWP
    result.put("BWP", new CurrencyDataImpl("BWP", "BWP", 2));
    // BYB
    result.put("BYB", new CurrencyDataImpl("BYB", "BYB", 130));
    // BYR
    result.put("BYR", new CurrencyDataImpl("BYR", "BYR", 0));
    // BZD
    result.put("BZD", new CurrencyDataImpl("BZD", "BZ$", 2));
    // CAD
    result.put("CAD", new CurrencyDataImpl("CAD", "CA$", 2, "C$"));
    // CDF
    result.put("CDF", new CurrencyDataImpl("CDF", "CDF", 2));
    // CHE
    result.put("CHE", new CurrencyDataImpl("CHE", "CHE", 2));
    // CHF
    result.put("CHF", new CurrencyDataImpl("CHF", "Fr.", 2, "CHF"));
    // CHW
    result.put("CHW", new CurrencyDataImpl("CHW", "CHW", 2));
    // CLE
    result.put("CLE", new CurrencyDataImpl("CLE", "Eº", 130));
    // CLF
    result.put("CLF", new CurrencyDataImpl("CLF", "CLF", 0));
    // CLP
    result.put("CLP", new CurrencyDataImpl("CLP", "CL$", 0, "CL$"));
    // CNY
    result.put("CNY", new CurrencyDataImpl("CNY", "CN¥", 2, "RMB¥"));
    // COP
    result.put("COP", new CurrencyDataImpl("COP", "CO$", 0, "COL$"));
    // COU
    result.put("COU", new CurrencyDataImpl("COU", "COU", 2));
    // CRC
    result.put("CRC", new CurrencyDataImpl("CRC", "₡", 0, "CR₡"));
    // CSD
    result.put("CSD", new CurrencyDataImpl("CSD", "CSD", 130));
    // CSK
    result.put("CSK", new CurrencyDataImpl("CSK", "CSK", 130));
    // CUC
    result.put("CUC", new CurrencyDataImpl("CUC", "CUC$", 2));
    // CUP
    result.put("CUP", new CurrencyDataImpl("CUP", "CU$", 2, "$MN"));
    // CVE
    result.put("CVE", new CurrencyDataImpl("CVE", "CV$", 2));
    // CYP
    result.put("CYP", new CurrencyDataImpl("CYP", "CY£", 130));
    // CZK
    result.put("CZK", new CurrencyDataImpl("CZK", "Kč", 2, "Kč"));
    // DDM
    result.put("DDM", new CurrencyDataImpl("DDM", "DDM", 130));
    // DEM
    result.put("DEM", new CurrencyDataImpl("DEM", "DM", 130));
    // DJF
    result.put("DJF", new CurrencyDataImpl("DJF", "Fdj", 0));
    // DKK
    result.put("DKK", new CurrencyDataImpl("DKK", "Dkr", 2, "kr"));
    // DOP
    result.put("DOP", new CurrencyDataImpl("DOP", "RD$", 2, "RD$"));
    // DZD
    result.put("DZD", new CurrencyDataImpl("DZD", "DA", 2));
    // ECS
    result.put("ECS", new CurrencyDataImpl("ECS", "ECS", 130));
    // ECV
    result.put("ECV", new CurrencyDataImpl("ECV", "ECV", 2));
    // EEK
    result.put("EEK", new CurrencyDataImpl("EEK", "Ekr", 2));
    // EGP
    result.put("EGP", new CurrencyDataImpl("EGP", "EG£", 2, "LE"));
    // EQE
    result.put("EQE", new CurrencyDataImpl("EQE", "EQE", 130));
    // ERN
    result.put("ERN", new CurrencyDataImpl("ERN", "Nfk", 2));
    // ESA
    result.put("ESA", new CurrencyDataImpl("ESA", "ESA", 2));
    // ESB
    result.put("ESB", new CurrencyDataImpl("ESB", "ESB", 2));
    // ESP
    result.put("ESP", new CurrencyDataImpl("ESP", "Pts", 128));
    // ETB
    result.put("ETB", new CurrencyDataImpl("ETB", "Br", 2));
    // EUR
    result.put("EUR", new CurrencyDataImpl("EUR", "€", 2, "€"));
    // FIM
    result.put("FIM", new CurrencyDataImpl("FIM", "mk", 130));
    // FJD
    result.put("FJD", new CurrencyDataImpl("FJD", "FJ$", 2));
    // FKP
    result.put("FKP", new CurrencyDataImpl("FKP", "FK£", 2));
    // FRF
    result.put("FRF", new CurrencyDataImpl("FRF", "₣", 130));
    // GBP
    result.put("GBP", new CurrencyDataImpl("GBP", "£", 2, "GB£"));
    // GEK
    result.put("GEK", new CurrencyDataImpl("GEK", "GEK", 130));
    // GEL
    result.put("GEL", new CurrencyDataImpl("GEL", "GEL", 2));
    // GHC
    result.put("GHC", new CurrencyDataImpl("GHC", "₵", 130));
    // GHS
    result.put("GHS", new CurrencyDataImpl("GHS", "GH₵", 2));
    // GIP
    result.put("GIP", new CurrencyDataImpl("GIP", "GI£", 2));
    // GMD
    result.put("GMD", new CurrencyDataImpl("GMD", "GMD", 2));
    // GNF
    result.put("GNF", new CurrencyDataImpl("GNF", "FG", 0));
    // GNS
    result.put("GNS", new CurrencyDataImpl("GNS", "GNS", 130));
    // GQE
    result.put("GQE", new CurrencyDataImpl("GQE", "GQE", 130));
    // GRD
    result.put("GRD", new CurrencyDataImpl("GRD", "₯", 130));
    // GTQ
    result.put("GTQ", new CurrencyDataImpl("GTQ", "GTQ", 2));
    // GWE
    result.put("GWE", new CurrencyDataImpl("GWE", "GWE", 130));
    // GWP
    result.put("GWP", new CurrencyDataImpl("GWP", "GWP", 2));
    // GYD
    result.put("GYD", new CurrencyDataImpl("GYD", "GY$", 0));
    // HKD
    result.put("HKD", new CurrencyDataImpl("HKD", "HK$", 2, "HK$"));
    // HNL
    result.put("HNL", new CurrencyDataImpl("HNL", "HNL", 2));
    // HRD
    result.put("HRD", new CurrencyDataImpl("HRD", "HRD", 130));
    // HRK
    result.put("HRK", new CurrencyDataImpl("HRK", "kn", 2));
    // HTG
    result.put("HTG", new CurrencyDataImpl("HTG", "HTG", 2));
    // HUF
    result.put("HUF", new CurrencyDataImpl("HUF", "Ft", 0));
    // IDR
    result.put("IDR", new CurrencyDataImpl("IDR", "Rp", 0));
    // IEP
    result.put("IEP", new CurrencyDataImpl("IEP", "IR£", 130));
    // ILP
    result.put("ILP", new CurrencyDataImpl("ILP", "I£", 130));
    // ILS
    result.put("ILS", new CurrencyDataImpl("ILS", "₪", 2, "IL₪"));
    // INR
    result.put("INR", new CurrencyDataImpl("INR", "Rs", 2, "Rs"));
    // IQD
    result.put("IQD", new CurrencyDataImpl("IQD", "IQD", 0));
    // IRR
    result.put("IRR", new CurrencyDataImpl("IRR", "IRR", 0));
    // ISK
    result.put("ISK", new CurrencyDataImpl("ISK", "Ikr", 0, "kr"));
    // ITL
    result.put("ITL", new CurrencyDataImpl("ITL", "IT₤", 128));
    // JMD
    result.put("JMD", new CurrencyDataImpl("JMD", "J$", 2, "JA$"));
    // JOD
    result.put("JOD", new CurrencyDataImpl("JOD", "JD", 3));
    // JPY
    result.put("JPY", new CurrencyDataImpl("JPY", "JP¥", 0, "JP¥"));
    // KES
    result.put("KES", new CurrencyDataImpl("KES", "Ksh", 2));
    // KGS
    result.put("KGS", new CurrencyDataImpl("KGS", "KGS", 2));
    // KHR
    result.put("KHR", new CurrencyDataImpl("KHR", "KHR", 2));
    // KMF
    result.put("KMF", new CurrencyDataImpl("KMF", "CF", 0));
    // KPW
    result.put("KPW", new CurrencyDataImpl("KPW", "KPW", 0));
    // KRW
    result.put("KRW", new CurrencyDataImpl("KRW", "₩", 0, "KR₩"));
    // KWD
    result.put("KWD", new CurrencyDataImpl("KWD", "KD", 3));
    // KYD
    result.put("KYD", new CurrencyDataImpl("KYD", "KY$", 2));
    // KZT
    result.put("KZT", new CurrencyDataImpl("KZT", "KZT", 2));
    // LAK
    result.put("LAK", new CurrencyDataImpl("LAK", "₭", 0));
    // LBP
    result.put("LBP", new CurrencyDataImpl("LBP", "LB£", 0));
    // LKR
    result.put("LKR", new CurrencyDataImpl("LKR", "SLRs", 2, "SLRs"));
    // LRD
    result.put("LRD", new CurrencyDataImpl("LRD", "L$", 2));
    // LSL
    result.put("LSL", new CurrencyDataImpl("LSL", "LSL", 2));
    // LSM
    result.put("LSM", new CurrencyDataImpl("LSM", "LSM", 130));
    // LTL
    result.put("LTL", new CurrencyDataImpl("LTL", "Lt", 2));
    // LTT
    result.put("LTT", new CurrencyDataImpl("LTT", "LTT", 130));
    // LUC
    result.put("LUC", new CurrencyDataImpl("LUC", "LUC", 2));
    // LUF
    result.put("LUF", new CurrencyDataImpl("LUF", "LUF", 128));
    // LUL
    result.put("LUL", new CurrencyDataImpl("LUL", "LUL", 2));
    // LVL
    result.put("LVL", new CurrencyDataImpl("LVL", "Ls", 2));
    // LVR
    result.put("LVR", new CurrencyDataImpl("LVR", "LVR", 130));
    // LYD
    result.put("LYD", new CurrencyDataImpl("LYD", "LD", 3));
    // MAD
    result.put("MAD", new CurrencyDataImpl("MAD", "MAD", 2));
    // MAF
    result.put("MAF", new CurrencyDataImpl("MAF", "MAF", 130));
    // MDL
    result.put("MDL", new CurrencyDataImpl("MDL", "MDL", 2));
    // MGA
    result.put("MGA", new CurrencyDataImpl("MGA", "MGA", 0));
    // MGF
    result.put("MGF", new CurrencyDataImpl("MGF", "MGF", 128));
    // MKD
    result.put("MKD", new CurrencyDataImpl("MKD", "MKD", 2));
    // MLF
    result.put("MLF", new CurrencyDataImpl("MLF", "MLF", 130));
    // MMK
    result.put("MMK", new CurrencyDataImpl("MMK", "MMK", 0));
    // MNT
    result.put("MNT", new CurrencyDataImpl("MNT", "₮", 0, "MN₮"));
    // MOP
    result.put("MOP", new CurrencyDataImpl("MOP", "MOP$", 2));
    // MRO
    result.put("MRO", new CurrencyDataImpl("MRO", "UM", 0));
    // MTL
    result.put("MTL", new CurrencyDataImpl("MTL", "Lm", 130));
    // MTP
    result.put("MTP", new CurrencyDataImpl("MTP", "MT£", 130));
    // MUR
    result.put("MUR", new CurrencyDataImpl("MUR", "MURs", 0));
    // MVR
    result.put("MVR", new CurrencyDataImpl("MVR", "MVR", 2));
    // MWK
    result.put("MWK", new CurrencyDataImpl("MWK", "MWK", 2));
    // MXN
    result.put("MXN", new CurrencyDataImpl("MXN", "Mex$", 2, "Mex$"));
    // MXP
    result.put("MXP", new CurrencyDataImpl("MXP", "MX$", 130));
    // MXV
    result.put("MXV", new CurrencyDataImpl("MXV", "MXV", 2));
    // MYR
    result.put("MYR", new CurrencyDataImpl("MYR", "RM", 2, "RM"));
    // MZE
    result.put("MZE", new CurrencyDataImpl("MZE", "MZE", 130));
    // MZM
    result.put("MZM", new CurrencyDataImpl("MZM", "Mt", 130));
    // MZN
    result.put("MZN", new CurrencyDataImpl("MZN", "MTn", 2));
    // NAD
    result.put("NAD", new CurrencyDataImpl("NAD", "N$", 2));
    // NGN
    result.put("NGN", new CurrencyDataImpl("NGN", "₦", 2));
    // NIC
    result.put("NIC", new CurrencyDataImpl("NIC", "NIC", 130));
    // NIO
    result.put("NIO", new CurrencyDataImpl("NIO", "C$", 2));
    // NLG
    result.put("NLG", new CurrencyDataImpl("NLG", "fl", 130));
    // NOK
    result.put("NOK", new CurrencyDataImpl("NOK", "Nkr", 2, "NOkr"));
    // NPR
    result.put("NPR", new CurrencyDataImpl("NPR", "NPRs", 2));
    // NZD
    result.put("NZD", new CurrencyDataImpl("NZD", "NZ$", 2));
    // OMR
    result.put("OMR", new CurrencyDataImpl("OMR", "OMR", 3));
    // PAB
    result.put("PAB", new CurrencyDataImpl("PAB", "B/.", 2, "B/."));
    // PEI
    result.put("PEI", new CurrencyDataImpl("PEI", "I/.", 130));
    // PEN
    result.put("PEN", new CurrencyDataImpl("PEN", "S/.", 2, "S/."));
    // PES
    result.put("PES", new CurrencyDataImpl("PES", "PES", 130));
    // PGK
    result.put("PGK", new CurrencyDataImpl("PGK", "PGK", 2));
    // PHP
    result.put("PHP", new CurrencyDataImpl("PHP", "₱", 2, "PHP"));
    // PKR
    result.put("PKR", new CurrencyDataImpl("PKR", "PKRs", 0, "PKRs."));
    // PLN
    result.put("PLN", new CurrencyDataImpl("PLN", "zł", 2));
    // PLZ
    result.put("PLZ", new CurrencyDataImpl("PLZ", "PLZ", 130));
    // PTE
    result.put("PTE", new CurrencyDataImpl("PTE", "Esc", 130));
    // PYG
    result.put("PYG", new CurrencyDataImpl("PYG", "₲", 0));
    // QAR
    result.put("QAR", new CurrencyDataImpl("QAR", "QR", 2));
    // RHD
    result.put("RHD", new CurrencyDataImpl("RHD", "RH$", 130));
    // ROL
    result.put("ROL", new CurrencyDataImpl("ROL", "ROL", 130));
    // RON
    result.put("RON", new CurrencyDataImpl("RON", "RON", 2));
    // RSD
    result.put("RSD", new CurrencyDataImpl("RSD", "din.", 0));
    // RUB
    result.put("RUB", new CurrencyDataImpl("RUB", "руб", 2, "руб"));
    // RUR
    result.put("RUR", new CurrencyDataImpl("RUR", "RUR", 130));
    // RWF
    result.put("RWF", new CurrencyDataImpl("RWF", "RWF", 0));
    // SAR
    result.put("SAR", new CurrencyDataImpl("SAR", "SR", 2, "SR"));
    // SBD
    result.put("SBD", new CurrencyDataImpl("SBD", "SI$", 2));
    // SCR
    result.put("SCR", new CurrencyDataImpl("SCR", "SRe", 2));
    // SDD
    result.put("SDD", new CurrencyDataImpl("SDD", "LSd", 130));
    // SDG
    result.put("SDG", new CurrencyDataImpl("SDG", "SDG", 2));
    // SDP
    result.put("SDP", new CurrencyDataImpl("SDP", "SDP", 130));
    // SEK
    result.put("SEK", new CurrencyDataImpl("SEK", "Skr", 2, "kr"));
    // SGD
    result.put("SGD", new CurrencyDataImpl("SGD", "S$", 2, "S$"));
    // SHP
    result.put("SHP", new CurrencyDataImpl("SHP", "SH£", 2));
    // SIT
    result.put("SIT", new CurrencyDataImpl("SIT", "SIT", 130));
    // SKK
    result.put("SKK", new CurrencyDataImpl("SKK", "Sk", 130));
    // SLL
    result.put("SLL", new CurrencyDataImpl("SLL", "Le", 0));
    // SOS
    result.put("SOS", new CurrencyDataImpl("SOS", "Ssh", 0));
    // SRD
    result.put("SRD", new CurrencyDataImpl("SRD", "SR$", 2));
    // SRG
    result.put("SRG", new CurrencyDataImpl("SRG", "Sf", 130));
    // STD
    result.put("STD", new CurrencyDataImpl("STD", "Db", 0));
    // SUR
    result.put("SUR", new CurrencyDataImpl("SUR", "SUR", 130));
    // SVC
    result.put("SVC", new CurrencyDataImpl("SVC", "SV₡", 130));
    // SYP
    result.put("SYP", new CurrencyDataImpl("SYP", "SY£", 0));
    // SZL
    result.put("SZL", new CurrencyDataImpl("SZL", "SZL", 2));
    // THB
    result.put("THB", new CurrencyDataImpl("THB", "฿", 2, "THB"));
    // TJR
    result.put("TJR", new CurrencyDataImpl("TJR", "TJR", 130));
    // TJS
    result.put("TJS", new CurrencyDataImpl("TJS", "TJS", 2));
    // TMM
    result.put("TMM", new CurrencyDataImpl("TMM", "TMM", 128));
    // TND
    result.put("TND", new CurrencyDataImpl("TND", "DT", 3));
    // TOP
    result.put("TOP", new CurrencyDataImpl("TOP", "T$", 2));
    // TPE
    result.put("TPE", new CurrencyDataImpl("TPE", "TPE", 130));
    // TRL
    result.put("TRL", new CurrencyDataImpl("TRL", "TRL", 128));
    // TRY
    result.put("TRY", new CurrencyDataImpl("TRY", "TL", 2, "YTL"));
    // TTD
    result.put("TTD", new CurrencyDataImpl("TTD", "TT$", 2));
    // TWD
    result.put("TWD", new CurrencyDataImpl("TWD", "NT$", 2, "NT$"));
    // TZS
    result.put("TZS", new CurrencyDataImpl("TZS", "TSh", 0));
    // UAH
    result.put("UAH", new CurrencyDataImpl("UAH", "₴", 2));
    // UAK
    result.put("UAK", new CurrencyDataImpl("UAK", "UAK", 130));
    // UGS
    result.put("UGS", new CurrencyDataImpl("UGS", "UGS", 130));
    // UGX
    result.put("UGX", new CurrencyDataImpl("UGX", "USh", 0));
    // USD
    result.put("USD", new CurrencyDataImpl("USD", "US$", 2, "US$"));
    // USN
    result.put("USN", new CurrencyDataImpl("USN", "USN", 2));
    // USS
    result.put("USS", new CurrencyDataImpl("USS", "USS", 2));
    // UYI
    result.put("UYI", new CurrencyDataImpl("UYI", "UYI", 2));
    // UYP
    result.put("UYP", new CurrencyDataImpl("UYP", "UYP", 130));
    // UYU
    result.put("UYU", new CurrencyDataImpl("UYU", "$U", 2, "UY$"));
    // UZS
    result.put("UZS", new CurrencyDataImpl("UZS", "UZS", 0));
    // VEB
    result.put("VEB", new CurrencyDataImpl("VEB", "VEB", 130));
    // VEF
    result.put("VEF", new CurrencyDataImpl("VEF", "Bs.F.", 2));
    // VND
    result.put("VND", new CurrencyDataImpl("VND", "₫", 24, "₫"));
    // VUV
    result.put("VUV", new CurrencyDataImpl("VUV", "VT", 0));
    // WST
    result.put("WST", new CurrencyDataImpl("WST", "WS$", 2));
    // XAF
    result.put("XAF", new CurrencyDataImpl("XAF", "FCFA", 0));
    // XAG
    result.put("XAG", new CurrencyDataImpl("XAG", "XAG", 2));
    // XAU
    result.put("XAU", new CurrencyDataImpl("XAU", "XAU", 2));
    // XBA
    result.put("XBA", new CurrencyDataImpl("XBA", "XBA", 2));
    // XBB
    result.put("XBB", new CurrencyDataImpl("XBB", "XBB", 2));
    // XBC
    result.put("XBC", new CurrencyDataImpl("XBC", "XBC", 2));
    // XBD
    result.put("XBD", new CurrencyDataImpl("XBD", "XBD", 2));
    // XCD
    result.put("XCD", new CurrencyDataImpl("XCD", "EC$", 2));
    // XDR
    result.put("XDR", new CurrencyDataImpl("XDR", "XDR", 2));
    // XEU
    result.put("XEU", new CurrencyDataImpl("XEU", "XEU", 2));
    // XFO
    result.put("XFO", new CurrencyDataImpl("XFO", "XFO", 2));
    // XFU
    result.put("XFU", new CurrencyDataImpl("XFU", "XFU", 2));
    // XOF
    result.put("XOF", new CurrencyDataImpl("XOF", "CFA", 0));
    // XPD
    result.put("XPD", new CurrencyDataImpl("XPD", "XPD", 2));
    // XPF
    result.put("XPF", new CurrencyDataImpl("XPF", "CFPF", 0));
    // XPT
    result.put("XPT", new CurrencyDataImpl("XPT", "XPT", 2));
    // XRE
    result.put("XRE", new CurrencyDataImpl("XRE", "XRE", 2));
    // XTS
    result.put("XTS", new CurrencyDataImpl("XTS", "XTS", 2));
    // XXX
    result.put("XXX", new CurrencyDataImpl("XXX", "XXX", 2));
    // YDD
    result.put("YDD", new CurrencyDataImpl("YDD", "YDD", 130));
    // YER
    result.put("YER", new CurrencyDataImpl("YER", "YR", 0, "YER"));
    // YUD
    result.put("YUD", new CurrencyDataImpl("YUD", "YUD", 130));
    // YUM
    result.put("YUM", new CurrencyDataImpl("YUM", "YUM", 130));
    // YUN
    result.put("YUN", new CurrencyDataImpl("YUN", "YUN", 130));
    // ZAL
    result.put("ZAL", new CurrencyDataImpl("ZAL", "ZAL", 2));
    // ZAR
    result.put("ZAR", new CurrencyDataImpl("ZAR", "R", 2, "ZAR"));
    // ZMK
    result.put("ZMK", new CurrencyDataImpl("ZMK", "ZK", 0));
    // ZRN
    result.put("ZRN", new CurrencyDataImpl("ZRN", "NZ", 130));
    // ZRZ
    result.put("ZRZ", new CurrencyDataImpl("ZRZ", "ZRZ", 130));
    // ZWD
    result.put("ZWD", new CurrencyDataImpl("ZWD", "Z$", 128));
    return result;
  }
  
  @Override
  protected JavaScriptObject loadCurrencyMapNative() {
    return overrideMap(super.loadCurrencyMapNative(), loadMyCurrencyMapOverridesNative());
  }
  
  private native JavaScriptObject loadMyCurrencyMapOverridesNative() /*-{
    return {
      // ADP
      "ADP": [ "ADP", "ADP", 128],
      // AED
      "AED": [ "AED", "DH", 2, "DH"],
      // AFA
      "AFA": [ "AFA", "AFA", 130],
      // AFN
      "AFN": [ "AFN", "Af", 0],
      // ALL
      "ALL": [ "ALL", "ALL", 0],
      // AMD
      "AMD": [ "AMD", "AMD", 0],
      // ANG
      "ANG": [ "ANG", "NAf.", 2],
      // AOA
      "AOA": [ "AOA", "Kz", 2],
      // AOK
      "AOK": [ "AOK", "AOK", 130],
      // AON
      "AON": [ "AON", "AON", 130],
      // AOR
      "AOR": [ "AOR", "AOR", 130],
      // ARA
      "ARA": [ "ARA", "₳", 130],
      // ARL
      "ARL": [ "ARL", "$L", 130],
      // ARM
      "ARM": [ "ARM", "m$n", 130],
      // ARP
      "ARP": [ "ARP", "ARP", 130],
      // ARS
      "ARS": [ "ARS", "AR$", 2, "AR$"],
      // ATS
      "ATS": [ "ATS", "ATS", 130],
      // AUD
      "AUD": [ "AUD", "AU$", 2, "AU$"],
      // AWG
      "AWG": [ "AWG", "Afl.", 2],
      // AZM
      "AZM": [ "AZM", "AZM", 130],
      // AZN
      "AZN": [ "AZN", "man.", 2],
      // BAD
      "BAD": [ "BAD", "BAD", 130],
      // BAM
      "BAM": [ "BAM", "KM", 2],
      // BBD
      "BBD": [ "BBD", "Bds$", 2],
      // BDT
      "BDT": [ "BDT", "Tk", 2, "Tk"],
      // BEC
      "BEC": [ "BEC", "BEC", 2],
      // BEF
      "BEF": [ "BEF", "BF", 130],
      // BEL
      "BEL": [ "BEL", "BEL", 2],
      // BGL
      "BGL": [ "BGL", "BGL", 130],
      // BGN
      "BGN": [ "BGN", "BGN", 2],
      // BHD
      "BHD": [ "BHD", "BD", 3],
      // BIF
      "BIF": [ "BIF", "FBu", 0],
      // BMD
      "BMD": [ "BMD", "BD$", 2],
      // BND
      "BND": [ "BND", "BN$", 2],
      // BOB
      "BOB": [ "BOB", "Bs", 2],
      // BOP
      "BOP": [ "BOP", "$b.", 130],
      // BOV
      "BOV": [ "BOV", "BOV", 2],
      // BRB
      "BRB": [ "BRB", "BRB", 130],
      // BRC
      "BRC": [ "BRC", "BRC", 130],
      // BRE
      "BRE": [ "BRE", "BRE", 130],
      // BRL
      "BRL": [ "BRL", "R$", 2, "R$"],
      // BRN
      "BRN": [ "BRN", "BRN", 130],
      // BRR
      "BRR": [ "BRR", "BRR", 130],
      // BSD
      "BSD": [ "BSD", "BS$", 2],
      // BTN
      "BTN": [ "BTN", "Nu.", 2],
      // BUK
      "BUK": [ "BUK", "BUK", 130],
      // BWP
      "BWP": [ "BWP", "BWP", 2],
      // BYB
      "BYB": [ "BYB", "BYB", 130],
      // BYR
      "BYR": [ "BYR", "BYR", 0],
      // BZD
      "BZD": [ "BZD", "BZ$", 2],
      // CAD
      "CAD": [ "CAD", "CA$", 2, "C$"],
      // CDF
      "CDF": [ "CDF", "CDF", 2],
      // CHE
      "CHE": [ "CHE", "CHE", 2],
      // CHF
      "CHF": [ "CHF", "Fr.", 2, "CHF"],
      // CHW
      "CHW": [ "CHW", "CHW", 2],
      // CLE
      "CLE": [ "CLE", "Eº", 130],
      // CLF
      "CLF": [ "CLF", "CLF", 0],
      // CLP
      "CLP": [ "CLP", "CL$", 0, "CL$"],
      // CNY
      "CNY": [ "CNY", "CN¥", 2, "RMB¥"],
      // COP
      "COP": [ "COP", "CO$", 0, "COL$"],
      // COU
      "COU": [ "COU", "COU", 2],
      // CRC
      "CRC": [ "CRC", "₡", 0, "CR₡"],
      // CSD
      "CSD": [ "CSD", "CSD", 130],
      // CSK
      "CSK": [ "CSK", "CSK", 130],
      // CUC
      "CUC": [ "CUC", "CUC$", 2],
      // CUP
      "CUP": [ "CUP", "CU$", 2, "$MN"],
      // CVE
      "CVE": [ "CVE", "CV$", 2],
      // CYP
      "CYP": [ "CYP", "CY£", 130],
      // CZK
      "CZK": [ "CZK", "Kč", 2, "Kč"],
      // DDM
      "DDM": [ "DDM", "DDM", 130],
      // DEM
      "DEM": [ "DEM", "DM", 130],
      // DJF
      "DJF": [ "DJF", "Fdj", 0],
      // DKK
      "DKK": [ "DKK", "Dkr", 2, "kr"],
      // DOP
      "DOP": [ "DOP", "RD$", 2, "RD$"],
      // DZD
      "DZD": [ "DZD", "DA", 2],
      // ECS
      "ECS": [ "ECS", "ECS", 130],
      // ECV
      "ECV": [ "ECV", "ECV", 2],
      // EEK
      "EEK": [ "EEK", "Ekr", 2],
      // EGP
      "EGP": [ "EGP", "EG£", 2, "LE"],
      // EQE
      "EQE": [ "EQE", "EQE", 130],
      // ERN
      "ERN": [ "ERN", "Nfk", 2],
      // ESA
      "ESA": [ "ESA", "ESA", 2],
      // ESB
      "ESB": [ "ESB", "ESB", 2],
      // ESP
      "ESP": [ "ESP", "Pts", 128],
      // ETB
      "ETB": [ "ETB", "Br", 2],
      // EUR
      "EUR": [ "EUR", "€", 2, "€"],
      // FIM
      "FIM": [ "FIM", "mk", 130],
      // FJD
      "FJD": [ "FJD", "FJ$", 2],
      // FKP
      "FKP": [ "FKP", "FK£", 2],
      // FRF
      "FRF": [ "FRF", "₣", 130],
      // GBP
      "GBP": [ "GBP", "£", 2, "GB£"],
      // GEK
      "GEK": [ "GEK", "GEK", 130],
      // GEL
      "GEL": [ "GEL", "GEL", 2],
      // GHC
      "GHC": [ "GHC", "₵", 130],
      // GHS
      "GHS": [ "GHS", "GH₵", 2],
      // GIP
      "GIP": [ "GIP", "GI£", 2],
      // GMD
      "GMD": [ "GMD", "GMD", 2],
      // GNF
      "GNF": [ "GNF", "FG", 0],
      // GNS
      "GNS": [ "GNS", "GNS", 130],
      // GQE
      "GQE": [ "GQE", "GQE", 130],
      // GRD
      "GRD": [ "GRD", "₯", 130],
      // GTQ
      "GTQ": [ "GTQ", "GTQ", 2],
      // GWE
      "GWE": [ "GWE", "GWE", 130],
      // GWP
      "GWP": [ "GWP", "GWP", 2],
      // GYD
      "GYD": [ "GYD", "GY$", 0],
      // HKD
      "HKD": [ "HKD", "HK$", 2, "HK$"],
      // HNL
      "HNL": [ "HNL", "HNL", 2],
      // HRD
      "HRD": [ "HRD", "HRD", 130],
      // HRK
      "HRK": [ "HRK", "kn", 2],
      // HTG
      "HTG": [ "HTG", "HTG", 2],
      // HUF
      "HUF": [ "HUF", "Ft", 0],
      // IDR
      "IDR": [ "IDR", "Rp", 0],
      // IEP
      "IEP": [ "IEP", "IR£", 130],
      // ILP
      "ILP": [ "ILP", "I£", 130],
      // ILS
      "ILS": [ "ILS", "₪", 2, "IL₪"],
      // INR
      "INR": [ "INR", "Rs", 2, "Rs"],
      // IQD
      "IQD": [ "IQD", "IQD", 0],
      // IRR
      "IRR": [ "IRR", "IRR", 0],
      // ISK
      "ISK": [ "ISK", "Ikr", 0, "kr"],
      // ITL
      "ITL": [ "ITL", "IT₤", 128],
      // JMD
      "JMD": [ "JMD", "J$", 2, "JA$"],
      // JOD
      "JOD": [ "JOD", "JD", 3],
      // JPY
      "JPY": [ "JPY", "JP¥", 0, "JP¥"],
      // KES
      "KES": [ "KES", "Ksh", 2],
      // KGS
      "KGS": [ "KGS", "KGS", 2],
      // KHR
      "KHR": [ "KHR", "KHR", 2],
      // KMF
      "KMF": [ "KMF", "CF", 0],
      // KPW
      "KPW": [ "KPW", "KPW", 0],
      // KRW
      "KRW": [ "KRW", "₩", 0, "KR₩"],
      // KWD
      "KWD": [ "KWD", "KD", 3],
      // KYD
      "KYD": [ "KYD", "KY$", 2],
      // KZT
      "KZT": [ "KZT", "KZT", 2],
      // LAK
      "LAK": [ "LAK", "₭", 0],
      // LBP
      "LBP": [ "LBP", "LB£", 0],
      // LKR
      "LKR": [ "LKR", "SLRs", 2, "SLRs"],
      // LRD
      "LRD": [ "LRD", "L$", 2],
      // LSL
      "LSL": [ "LSL", "LSL", 2],
      // LSM
      "LSM": [ "LSM", "LSM", 130],
      // LTL
      "LTL": [ "LTL", "Lt", 2],
      // LTT
      "LTT": [ "LTT", "LTT", 130],
      // LUC
      "LUC": [ "LUC", "LUC", 2],
      // LUF
      "LUF": [ "LUF", "LUF", 128],
      // LUL
      "LUL": [ "LUL", "LUL", 2],
      // LVL
      "LVL": [ "LVL", "Ls", 2],
      // LVR
      "LVR": [ "LVR", "LVR", 130],
      // LYD
      "LYD": [ "LYD", "LD", 3],
      // MAD
      "MAD": [ "MAD", "MAD", 2],
      // MAF
      "MAF": [ "MAF", "MAF", 130],
      // MDL
      "MDL": [ "MDL", "MDL", 2],
      // MGA
      "MGA": [ "MGA", "MGA", 0],
      // MGF
      "MGF": [ "MGF", "MGF", 128],
      // MKD
      "MKD": [ "MKD", "MKD", 2],
      // MLF
      "MLF": [ "MLF", "MLF", 130],
      // MMK
      "MMK": [ "MMK", "MMK", 0],
      // MNT
      "MNT": [ "MNT", "₮", 0, "MN₮"],
      // MOP
      "MOP": [ "MOP", "MOP$", 2],
      // MRO
      "MRO": [ "MRO", "UM", 0],
      // MTL
      "MTL": [ "MTL", "Lm", 130],
      // MTP
      "MTP": [ "MTP", "MT£", 130],
      // MUR
      "MUR": [ "MUR", "MURs", 0],
      // MVR
      "MVR": [ "MVR", "MVR", 2],
      // MWK
      "MWK": [ "MWK", "MWK", 2],
      // MXN
      "MXN": [ "MXN", "Mex$", 2, "Mex$"],
      // MXP
      "MXP": [ "MXP", "MX$", 130],
      // MXV
      "MXV": [ "MXV", "MXV", 2],
      // MYR
      "MYR": [ "MYR", "RM", 2, "RM"],
      // MZE
      "MZE": [ "MZE", "MZE", 130],
      // MZM
      "MZM": [ "MZM", "Mt", 130],
      // MZN
      "MZN": [ "MZN", "MTn", 2],
      // NAD
      "NAD": [ "NAD", "N$", 2],
      // NGN
      "NGN": [ "NGN", "₦", 2],
      // NIC
      "NIC": [ "NIC", "NIC", 130],
      // NIO
      "NIO": [ "NIO", "C$", 2],
      // NLG
      "NLG": [ "NLG", "fl", 130],
      // NOK
      "NOK": [ "NOK", "Nkr", 2, "NOkr"],
      // NPR
      "NPR": [ "NPR", "NPRs", 2],
      // NZD
      "NZD": [ "NZD", "NZ$", 2],
      // OMR
      "OMR": [ "OMR", "OMR", 3],
      // PAB
      "PAB": [ "PAB", "B/.", 2, "B/."],
      // PEI
      "PEI": [ "PEI", "I/.", 130],
      // PEN
      "PEN": [ "PEN", "S/.", 2, "S/."],
      // PES
      "PES": [ "PES", "PES", 130],
      // PGK
      "PGK": [ "PGK", "PGK", 2],
      // PHP
      "PHP": [ "PHP", "₱", 2, "PHP"],
      // PKR
      "PKR": [ "PKR", "PKRs", 0, "PKRs."],
      // PLN
      "PLN": [ "PLN", "zł", 2],
      // PLZ
      "PLZ": [ "PLZ", "PLZ", 130],
      // PTE
      "PTE": [ "PTE", "Esc", 130],
      // PYG
      "PYG": [ "PYG", "₲", 0],
      // QAR
      "QAR": [ "QAR", "QR", 2],
      // RHD
      "RHD": [ "RHD", "RH$", 130],
      // ROL
      "ROL": [ "ROL", "ROL", 130],
      // RON
      "RON": [ "RON", "RON", 2],
      // RSD
      "RSD": [ "RSD", "din.", 0],
      // RUB
      "RUB": [ "RUB", "руб", 2, "руб"],
      // RUR
      "RUR": [ "RUR", "RUR", 130],
      // RWF
      "RWF": [ "RWF", "RWF", 0],
      // SAR
      "SAR": [ "SAR", "SR", 2, "SR"],
      // SBD
      "SBD": [ "SBD", "SI$", 2],
      // SCR
      "SCR": [ "SCR", "SRe", 2],
      // SDD
      "SDD": [ "SDD", "LSd", 130],
      // SDG
      "SDG": [ "SDG", "SDG", 2],
      // SDP
      "SDP": [ "SDP", "SDP", 130],
      // SEK
      "SEK": [ "SEK", "Skr", 2, "kr"],
      // SGD
      "SGD": [ "SGD", "S$", 2, "S$"],
      // SHP
      "SHP": [ "SHP", "SH£", 2],
      // SIT
      "SIT": [ "SIT", "SIT", 130],
      // SKK
      "SKK": [ "SKK", "Sk", 130],
      // SLL
      "SLL": [ "SLL", "Le", 0],
      // SOS
      "SOS": [ "SOS", "Ssh", 0],
      // SRD
      "SRD": [ "SRD", "SR$", 2],
      // SRG
      "SRG": [ "SRG", "Sf", 130],
      // STD
      "STD": [ "STD", "Db", 0],
      // SUR
      "SUR": [ "SUR", "SUR", 130],
      // SVC
      "SVC": [ "SVC", "SV₡", 130],
      // SYP
      "SYP": [ "SYP", "SY£", 0],
      // SZL
      "SZL": [ "SZL", "SZL", 2],
      // THB
      "THB": [ "THB", "฿", 2, "THB"],
      // TJR
      "TJR": [ "TJR", "TJR", 130],
      // TJS
      "TJS": [ "TJS", "TJS", 2],
      // TMM
      "TMM": [ "TMM", "TMM", 128],
      // TND
      "TND": [ "TND", "DT", 3],
      // TOP
      "TOP": [ "TOP", "T$", 2],
      // TPE
      "TPE": [ "TPE", "TPE", 130],
      // TRL
      "TRL": [ "TRL", "TRL", 128],
      // TRY
      "TRY": [ "TRY", "TL", 2, "YTL"],
      // TTD
      "TTD": [ "TTD", "TT$", 2],
      // TWD
      "TWD": [ "TWD", "NT$", 2, "NT$"],
      // TZS
      "TZS": [ "TZS", "TSh", 0],
      // UAH
      "UAH": [ "UAH", "₴", 2],
      // UAK
      "UAK": [ "UAK", "UAK", 130],
      // UGS
      "UGS": [ "UGS", "UGS", 130],
      // UGX
      "UGX": [ "UGX", "USh", 0],
      // USD
      "USD": [ "USD", "US$", 2, "US$"],
      // USN
      "USN": [ "USN", "USN", 2],
      // USS
      "USS": [ "USS", "USS", 2],
      // UYI
      "UYI": [ "UYI", "UYI", 2],
      // UYP
      "UYP": [ "UYP", "UYP", 130],
      // UYU
      "UYU": [ "UYU", "$U", 2, "UY$"],
      // UZS
      "UZS": [ "UZS", "UZS", 0],
      // VEB
      "VEB": [ "VEB", "VEB", 130],
      // VEF
      "VEF": [ "VEF", "Bs.F.", 2],
      // VND
      "VND": [ "VND", "₫", 24, "₫"],
      // VUV
      "VUV": [ "VUV", "VT", 0],
      // WST
      "WST": [ "WST", "WS$", 2],
      // XAF
      "XAF": [ "XAF", "FCFA", 0],
      // XAG
      "XAG": [ "XAG", "XAG", 2],
      // XAU
      "XAU": [ "XAU", "XAU", 2],
      // XBA
      "XBA": [ "XBA", "XBA", 2],
      // XBB
      "XBB": [ "XBB", "XBB", 2],
      // XBC
      "XBC": [ "XBC", "XBC", 2],
      // XBD
      "XBD": [ "XBD", "XBD", 2],
      // XCD
      "XCD": [ "XCD", "EC$", 2],
      // XDR
      "XDR": [ "XDR", "XDR", 2],
      // XEU
      "XEU": [ "XEU", "XEU", 2],
      // XFO
      "XFO": [ "XFO", "XFO", 2],
      // XFU
      "XFU": [ "XFU", "XFU", 2],
      // XOF
      "XOF": [ "XOF", "CFA", 0],
      // XPD
      "XPD": [ "XPD", "XPD", 2],
      // XPF
      "XPF": [ "XPF", "CFPF", 0],
      // XPT
      "XPT": [ "XPT", "XPT", 2],
      // XRE
      "XRE": [ "XRE", "XRE", 2],
      // XTS
      "XTS": [ "XTS", "XTS", 2],
      // XXX
      "XXX": [ "XXX", "XXX", 2],
      // YDD
      "YDD": [ "YDD", "YDD", 130],
      // YER
      "YER": [ "YER", "YR", 0, "YER"],
      // YUD
      "YUD": [ "YUD", "YUD", 130],
      // YUM
      "YUM": [ "YUM", "YUM", 130],
      // YUN
      "YUN": [ "YUN", "YUN", 130],
      // ZAL
      "ZAL": [ "ZAL", "ZAL", 2],
      // ZAR
      "ZAR": [ "ZAR", "R", 2, "ZAR"],
      // ZMK
      "ZMK": [ "ZMK", "ZK", 0],
      // ZRN
      "ZRN": [ "ZRN", "NZ", 130],
      // ZRZ
      "ZRZ": [ "ZRZ", "ZRZ", 130],
      // ZWD
      "ZWD": [ "ZWD", "Z$", 128],
    };
  }-*/;
}
