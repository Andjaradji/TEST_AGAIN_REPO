package com.vexanium.vexgift.bean.fixture;

import com.vexanium.vexgift.bean.model.Brand;
import com.vexanium.vexgift.bean.model.Notification;
import com.vexanium.vexgift.bean.model.Voucher;
import com.vexanium.vexgift.bean.response.VoucherResponse;

import java.util.ArrayList;
import java.util.Random;

import static com.vexanium.vexgift.app.StaticGroup.NORMAL_COUPON;

/**
 * Created by hizkia on 14/03/18.
 */

public class FixtureData {

    public static int notifCount = 10;

    public static Random random = new Random();

    public static final ArrayList<String> monthList = new ArrayList<String>() {
        {
            add("January");
            add("February");
            add("March");
            add("April");
            add("May");
            add("June");
            add("July");
        }
    };

    public static final ArrayList<String> typeList = new ArrayList<String>() {
        {
            add("Free");
            add("Pay With Vex Point");
            add("Coming soon");
            add("Cashback");
            add("Premium");
        }
    };

    public static final ArrayList<String> categoryList = new ArrayList<String>() {
        {
            add("Food and Beverages");
            add("Holiday");
            add("Beauty and Spa");
            add("Sport");
            add("Entertainment");
            add("Activity");
        }
    };

    public static final ArrayList<String> locationList = new ArrayList<String>() {
        {
            add("Indonesia");
            add("Singapore");
            add("Malaysia");
            add("Philipine");
        }
    };

    public static final ArrayList<String> notifTypes = new ArrayList<String>() {
        {
            add("exp");
            add("exp_soon");
            add("avail");
        }
    };

    public static final ArrayList<String> voucherTitle = new ArrayList<String>() {
        {

            add("Buy 1 Get 1 VexBread All Varians");
            add("Harga Spesial Vex Essential Facial Spa Untuk Semua Jenis Kulit seharga Rp. 125.000");
            add("Diskon 50% Gel Polish dari The Vex Nail Shop");
        }
    };

    public static final ArrayList<String> voucherPhotoUrl = new ArrayList<String>() {
        {
            add("http://scdn.ctree.id/f/180314/1521038749930_pizza.webp");
            add("https://vinylbannersprinting.co.uk/wp-content/uploads/2016/05/RB19-um-demo.png");
            add("http://smnworld.com/wp-content/uploads/2016/10/Banner-Promo-Web-Oktober-2016.jpg");
            add("https://qupunyadia.files.wordpress.com/2013/08/banner-promo-55rb.jpg");
        }
    };

    public static final ArrayList<String> brandTitle = new ArrayList<String>() {
        {
            add("Vex Airdrop");
            add("Pizzeria");
            add("Vexanium");
            add("Vexbook");
            add("Vexipedia");
        }
    };

    public static final ArrayList<String> brandPhotoUrl = new ArrayList<String>() {
        {
            add("https://www.overleaf.com/assets/logos/overleaf_og_logo.png");
            add("http://scdn.ctree.id/f/180314/1521038888014_piz.webp");
        }
    };

    public static final ArrayList<VoucherResponse> showCaseVoucherResponse = new ArrayList<VoucherResponse>() {
        {
            add(new VoucherResponse(
                            new Voucher(
                                    "http://scdn.ctree.id/f/180321/1521569817922_achain.webp",
                                    "FREE 10 COINS! Achain Airdrop",
                                    (random.nextInt(27) + 1) + " " + getRandomData(monthList) + " 2018"),
                            (random.nextInt(10) + 10) * 50,
                            (random.nextInt(10)) * 50,
                            NORMAL_COUPON
                    )
            );
            add(new VoucherResponse(
                            new Voucher(
                                    "http://scdn.ctree.id/f/180321/1521569916569_flower advisor.webp",
                                    "Be Her Early Valentine, 15% off Discount",
                                    (random.nextInt(27) + 1) + " " + getRandomData(monthList) + " 2018"),
                            (random.nextInt(10) + 10) * 50,
                            (random.nextInt(10)) * 50,
                            NORMAL_COUPON
                    )
            );
            add(new VoucherResponse(
                            new Voucher(
                                    "http://scdn.ctree.id/f/180321/1521570356638_Orori Voucher.webp",
                                    "Orori Instant Voucher Rp750.000,-",
                                    (random.nextInt(27) + 1) + " " + getRandomData(monthList) + " 2018"),
                            (random.nextInt(10) + 10) * 50,
                            (random.nextInt(10)) * 50,
                            NORMAL_COUPON
                    )
            );
            add(new VoucherResponse(
                            new Voucher(
                                    "http://scdn.ctree.id/f/180321/1521570226785_kcash.webp",
                                    "FREE 10 COINS! KCash Airdrop",
                                    (random.nextInt(27) + 1) + " " + getRandomData(monthList) + " 2018"),
                            (random.nextInt(10) + 10) * 50,
                            (random.nextInt(10)) * 50,
                            NORMAL_COUPON
                    )
            );
            add(new VoucherResponse(
                            new Voucher(
                                    "http://scdn.ctree.id/f/180321/1521570502012_tokenomy.webp",
                                    "FREE 20 COINS! Tokenomy Airdrop",
                                    (random.nextInt(27) + 1) + " " + getRandomData(monthList) + " 2018"),
                            (random.nextInt(10) + 10) * 50,
                            (random.nextInt(10)) * 50,
                            NORMAL_COUPON
                    )
            );
        }
    };

//    public static final ArrayList<Brand> showCaseBrand = new ArrayList<Brand>() {
//        {
//            clear();
//            for (VoucherResponse voucherResponse : showCaseVoucherResponse) {
//                add(voucherResponse.getVoucher().getBrand());
//            }
//        }
//    };

    public static final ArrayList<Brand> getRandomBrands(int brandCount) {
        ArrayList<Brand> brands = new ArrayList<>();
        for (int i = 0; i < brandCount; i++) {
            brands.add(new Brand(getRandomData(brandPhotoUrl), getRandomData(brandTitle)));
        }
        return brands;
    }

    public static final Brand getRandomBrand() {
         return new Brand(getRandomData(brandPhotoUrl), getRandomData(brandTitle));
    }


    public static ArrayList<String> getRandomData(int count, ArrayList<String> arr) {
        ArrayList<String> list = new ArrayList<>();
        int i = 0;
        do {
            int idx = random.nextInt(arr.size());
            if (!list.contains(arr.get(idx))) {
                list.add(arr.get(idx));
                i++;
            }
        } while (i != count);

        return list;
    }

    public static String getRandomData(ArrayList<String> arr) {
        int idx = random.nextInt(arr.size());
        return arr.get(idx);

    }

    public static final ArrayList<Notification> notifs = new ArrayList<Notification>() {
        {
            for (int i = 0; i < notifCount; i++) {
                Notification notification = new Notification();
                notification.setType(getRandomData(notifTypes));
                notification.setVoucher(showCaseVoucherResponse.get(random.nextInt(showCaseVoucherResponse.size())).getVoucher()
                );

                notification.setNew(false);

                add(notification);
            }
            get(0).setNew(true);
        }
    };

//    public static final ArrayList<Brand> getRandomBrand(int brandCount) {
//        ArrayList<Brand> brands = new ArrayList<>();
//        for (int i = 0; i < brandCount; i++) {
//            brands.add(new Brand(getRandomData(brandPhotoUrl), getRandomData(brandTitle)));
//        }
//        return brands;
//    }

    public static final ArrayList<Voucher> getRandomVoucher(int voucherCount, boolean active) {
        ArrayList<Voucher> vouchers = new ArrayList<>();
        for (int i = 0; i < voucherCount; i++) {
            Voucher voucher = showCaseVoucherResponse.get(random.nextInt(showCaseVoucherResponse.size())).getVoucher();
            voucher.active = active;
            vouchers.add(voucher);
        }
        return vouchers;
    }

    public static final ArrayList<VoucherResponse> getRandomVoucherResponse(int voucherCount, boolean active) {
        ArrayList<VoucherResponse> vouchers = new ArrayList<>();
//        for (VoucherResponse voucherResponse : showCaseVoucherResponse) {
//            vouchers.add(voucherResponse);
//        }
        for (int i = 0; i < voucherCount; i++) {
            VoucherResponse voucherResponse = new VoucherResponse();
            voucherResponse.setVoucher(new Voucher(
                    getRandomData(voucherPhotoUrl), getRandomData(voucherTitle), (random.nextInt(29) + 1) + " April 2018",
                     active)
            );
            voucherResponse.setStock((random.nextInt(19) + 1) * 50);
            voucherResponse.setAvail(voucherResponse.getStock() - random.nextInt(50));

            vouchers.add(voucherResponse);
        }

        return vouchers;
    }
}
