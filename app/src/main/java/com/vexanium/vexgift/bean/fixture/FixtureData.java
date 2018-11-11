package com.vexanium.vexgift.bean.fixture;

import com.vexanium.vexgift.R;

import java.util.ArrayList;

/**
 * Created by hizkia on 14/03/18.
 */

public class FixtureData {

    public static final ArrayList<String> documentType = new ArrayList<String>() {
        {
            add("ID Card");
            add("Passport");
            add("Driving License");
        }
    };

    public static final ArrayList<String> countries = new ArrayList<String>() {
        {
            add("Afghanistan");
            add("Albania");
            add("Algeria");
            add("Andorra");
            add("Angola");
            add("Antigua & Deps");
            add("Argentina");
            add("Armenia");
            add("Australia");
            add("Austria");
            add("Azerbaijan");
            add("Bahamas");
            add("Bahrain");
            add("Bangladesh");
            add("Barbados");
            add("Belarus");
            add("Belgium");
            add("Belize");
            add("Benin");
            add("Bhutan");
            add("Bolivia");
            add("Bosnia Herzegovina");
            add("Botswana");
            add("Brazil");
            add("Brunei");
            add("Bulgaria");
            add("Burkina");
            add("Burundi");
            add("Cambodia");
            add("Cameroon");
            add("Canada");
            add("Cape Verde");
            add("Central African Rep");
            add("Chad");
            add("Chile");
            add("China");
            add("Colombia");
            add("Comoros");
            add("Congo");
            add("Congo {Democratic Rep}");
            add("Costa Rica");
            add("Croatia");
            add("Cuba");
            add("Cyprus");
            add("Czech Republic");
            add("Denmark");
            add("Djibouti");
            add("Dominica");
            add("Dominican Republic");
            add("East Timor");
            add("Ecuador");
            add("Egypt");
            add("El Salvador");
            add("Equatorial Guinea");
            add("Eritrea");
            add("Estonia");
            add("Ethiopia");
            add("Fiji");
            add("Finland");
            add("France");
            add("Gabon");
            add("Gambia");
            add("Georgia");
            add("Germany");
            add("Ghana");
            add("Greece");
            add("Grenada");
            add("Guatemala");
            add("Guinea");
            add("Guinea-Bissau");
            add("Guyana");
            add("Haiti");
            add("Honduras");
            add("Hungary");
            add("Iceland");
            add("India");
            add("Indonesia");
            add("Iran");
            add("Iraq");
            add("Ireland {Republic}");
            add("Israel");
            add("Italy");
            add("Ivory Coast");
            add("Jamaica");
            add("Japan");
            add("Jordan");
            add("Kazakhstan");
            add("Kenya");
            add("Kiribati");
            add("Korea North");
            add("Korea South");
            add("Kosovo");
            add("Kuwait");
            add("Kyrgyzstan");
            add("Laos");
            add("Latvia");
            add("Lebanon");
            add("Lesotho");
            add("Liberia");
            add("Libya");
            add("Liechtenstein");
            add("Lithuania");
            add("Luxembourg");
            add("Macedonia");
            add("Madagascar");
            add("Malawi");
            add("Malaysia");
            add("Maldives");
            add("Mali");
            add("Malta");
            add("Marshall Islands");
            add("Mauritania");
            add("Mauritius");
            add("Mexico");
            add("Micronesia");
            add("Moldova");
            add("Monaco");
            add("Mongolia");
            add("Montenegro");
            add("Morocco");
            add("Mozambique");
            add("Myanmar, {Burma}");
            add("Namibia");
            add("Nauru");
            add("Nepal");
            add("Netherlands");
            add("New Zealand");
            add("Nicaragua");
            add("Niger");
            add("Nigeria");
            add("Norway");
            add("Oman");
            add("Pakistan");
            add("Palau");
            add("Panama");
            add("Papua New Guinea");
            add("Paraguay");
            add("Peru");
            add("Philippines");
            add("Poland");
            add("Portugal");
            add("Qatar");
            add("Romania");
            add("Russian Federation");
            add("Rwanda");
            add("St Kitts & Nevis");
            add("St Lucia");
            add("Saint Vincent & the Grenadines");
            add("Samoa");
            add("San Marino");
            add("Sao Tome & Principe");
            add("Saudi Arabia");
            add("Senegal");
            add("Serbia");
            add("Seychelles");
            add("Sierra Leone");
            add("Singapore");
            add("Slovakia");
            add("Slovenia");
            add("Solomon Islands");
            add("Somalia");
            add("South Africa");
            add("South Sudan");
            add("Spain");
            add("Sri Lanka");
            add("Sudan");
            add("Suriname");
            add("Swaziland");
            add("Sweden");
            add("Switzerland");
            add("Syria");
            add("Taiwan");
            add("Tajikistan");
            add("Tanzania");
            add("Thailand");
            add("Togo");
            add("Tonga");
            add("Trinidad & Tobago");
            add("Tunisia");
            add("Turkey");
            add("Turkmenistan");
            add("Tuvalu");
            add("Uganda");
            add("Ukraine");
            add("United Arab Emirates");
            add("United Kingdom");
            add("United States");
            add("Uruguay");
            add("Uzbekistan");
            add("Vanuatu");
            add("Vatican City");
            add("Venezuela");
            add("Vietnam");
            add("Yemen");
            add("Zambia");
            add("Zimbabwe");
        }
    };

    public static final ArrayList<Exchanger> exchangerList = new ArrayList<Exchanger>() {
        {
            add(new Exchanger("BTC-alpha", "https://btc-alpha.com/exchange/VEX_BTC/"));
            add(new Exchanger("Tokenomy", "https://exchange.tokenomy.com/login"));
            add(new Exchanger("Indodax", "https://indodax.com/"));
            add(new Exchanger("Exrates", "https://exrates.me/dashboard"));
            add(new Exchanger("Sistemkoin", "https://sistemkoin.com/#/"));
            add(new Exchanger("Bitinka", "https://www.bitinka.com/pe/bitinka/home"));
        }
    };


    public static final ArrayList<WalletToken> tokenList = new ArrayList<WalletToken>() {
        {
            add(new WalletToken(1, "BTC", R.drawable.bitcoin_logo, 1.2434f, 101774808));
            add(new WalletToken(2, "VEX", R.drawable.vex_logo, 560213.584f, 195));
        }
    };

    public static final ArrayList<WalletTokenRecord> tokenRecordList = new ArrayList<WalletTokenRecord>() {
        {
            add(new WalletTokenRecord(1, 1, "ACTsnied34dfcbjawye", WalletTokenRecord.SEND, 1.1f, "16-08-2018 15:12 GMT"));
            add(new WalletTokenRecord(2, 1, "ACTse32vre2rf2ffds4", WalletTokenRecord.RECEIVE, 3.3434f, "16-08-2018 12:34 GMT"));
            add(new WalletTokenRecord(2, 2, "ACTg099mfclrsitj34j", WalletTokenRecord.RECEIVE, 560213.584f, "14-08-2018 03:34 GMT"));
        }
    };

}
