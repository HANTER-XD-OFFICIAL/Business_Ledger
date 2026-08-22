package com.example.data.model

enum class TransactionType(val labelBn: String, val labelEn: String) {
    SALE("বিক্রয়", "Sale"),
    PURCHASE("ক্রয়", "Purchase"),
    EXPENSE("খরচ", "Expense"),
    INCOME("অন্যান্য আয়", "Income"),
    DUE_COLLECTION("বাকি আদায়", "Due Collection"),
    DUE_PAYMENT("দেনা পরিশোধ", "Due Payment")
}

enum class PartyType(val labelBn: String, val labelEn: String) {
    CUSTOMER("কাস্টমার", "Customer"),
    SUPPLIER("সাপ্লায়ার", "Supplier")
}

enum class PaymentMode(val labelBn: String, val labelEn: String) {
    CASH("নগদ", "Cash"),
    BKASH("বিকাশ", "bKash"),
    NAGAD("নগদ", "Nagad"),
    ROCKET("রকেট", "Rocket"),
    BANK("ব্যাংক", "Bank Transfer"),
    DUE("বাকি", "Due")
}

enum class ExpenseCategory(val labelBn: String, val labelEn: String) {
    SHOP_RENT("দোকান ভাড়া", "Shop Rent"),
    ELECTRICITY_BILL("বিদ্যুৎ ও ইউটিলিটি", "Electricity & Bills"),
    SALARY("কর্মচারীর বেতন", "Staff Salary"),
    TRANSPORT("যাতায়াত ও ভাড়া", "Transport & Delivery"),
    FOOD_REFRESHMENT("আপ্যায়ন ও নাস্তা", "Refreshments"),
    PACKAGING("প্যাকেজিং ও ব্যাগ", "Packaging"),
    REPAIR_MAINTENANCE("মেরামত ও সার্ভিস", "Maintenance"),
    OTHER("অন্যান্য খরচ", "Other Expense")
}

enum class ProductUnit(val labelBn: String, val symbol: String) {
    PIECE("পিস / টি", "pcs"),
    KG("কেজি", "kg"),
    GRAM("গ্রাম", "gm"),
    LITER("লিটার", "L"),
    PACKET("প্যাকেট", "pkt"),
    BOX("বক্স / কার্টন", "box"),
    DOZEN("ডজন", "dzn")
}
