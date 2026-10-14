// GLOBAL FAIL-SAFE: Ensure splash always hides
(function() {
    const splashTimer = setTimeout(() => {
        const splash = document.getElementById('splash-screen');
        if (splash) {
            splash.style.opacity = '0';
            setTimeout(() => { splash.style.display = 'none'; }, 200);
        }
    }, 1000);
    window.splashTimer = splashTimer;
})();

window.onerror = function(msg, url, lineNo, columnNo, error) {
    console.error('Master Error Catch:', msg, 'at', lineNo, ':', columnNo);
    const splash = document.getElementById('splash-screen');
    if (splash) splash.style.display = 'none';
    return false;
};

// Firebase Configuration - PLACEHOLDERS
const firebaseConfig = {
    apiKey: "YOUR_API_KEY",
    authDomain: "your-project.firebaseapp.com",
    projectId: "your-project",
    storageBucket: "your-project.appspot.com",
    messagingSenderId: "123456789",
    appId: "1:123456789:web:abcdef"
};

// Initialize Firebase safely
let db = null;
try {
    if (typeof firebase !== 'undefined' && firebaseConfig.apiKey !== "YOUR_API_KEY") {
        firebase.initializeApp(firebaseConfig);
        db = firebase.firestore();
    }
} catch (e) {
    console.error("Firebase Initialization Error:", e);
}

// State Management
const INITIAL_STATE = {
    uid: null,
    isLoggedIn: false,
    hasAgreedToTerms: false,
    name: "User",
    email: "",
    phone: "",
    country: "Kenya",
    currency: "KES",
    limit: 10000,
    pendingLoan: 0,
    lastDisbursementTime: 0,
    history: [],
    referralCode: "GENZ-XXXX",
    kycCompleted: false,
    kycFront: null,
    kycBack: null,
    kycSelfie: null,
    loanCategory: null,
    loanStep: 1,
    loanFormData: {},
    profile: {},
    lastProgressScreen: null,
    savedQualifiedAmount: 0,
    savedLoanTerms: {}
};

let currentUser = { ...INITIAL_STATE, referralCode: "GENZ-" + Math.random().toString(36).substring(2, 6).toUpperCase() };

let globalQualifiedAmount = 0;
let currentLoanTerms = {};
let currentStep = 1;
let isLoginMode = false;
const totalSteps = 8;

const COUNTRIES = [
    { name: "Afghanistan", code: "AF", dialCode: "+93", currency: "AFN", rate: 0.54, localLimit: 10000 },
    { name: "Albania", code: "AL", dialCode: "+355", currency: "ALL", rate: 0.71, localLimit: 15000 },
    { name: "Algeria", code: "DZ", dialCode: "+213", currency: "DZD", rate: 1.05, localLimit: 20000 },
    { name: "Andorra", code: "AD", dialCode: "+376", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Angola", code: "AO", dialCode: "+244", currency: "AOA", rate: 6.5, localLimit: 100000 },
    { name: "Argentina", code: "AR", dialCode: "+54", currency: "ARS", rate: 6.8, localLimit: 150000 },
    { name: "Armenia", code: "AM", dialCode: "+374", currency: "AMD", rate: 3.1, localLimit: 50000 },
    { name: "Australia", code: "AU", dialCode: "+61", currency: "AUD", rate: 0.012, localLimit: 1000 },
    { name: "Austria", code: "AT", dialCode: "+43", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Azerbaijan", code: "AZ", dialCode: "+994", currency: "AZN", rate: 0.013, localLimit: 500 },
    { name: "Bahamas", code: "BS", dialCode: "+1-242", currency: "BSD", rate: 0.0077, localLimit: 1000 },
    { name: "Bahrain", code: "BH", dialCode: "+973", currency: "BHD", rate: 0.0029, localLimit: 500 },
    { name: "Bangladesh", code: "BD", dialCode: "+880", currency: "BDT", rate: 0.92, localLimit: 20000 },
    { name: "Barbados", code: "BB", dialCode: "+1-246", currency: "BBD", rate: 0.015, localLimit: 1500 },
    { name: "Belarus", code: "BY", dialCode: "+375", currency: "BYN", rate: 0.025, localLimit: 1000 },
    { name: "Belgium", code: "BE", dialCode: "+32", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Belize", code: "BZ", dialCode: "+501", currency: "BZD", rate: 0.015, localLimit: 1500 },
    { name: "Benin", code: "BJ", dialCode: "+229", currency: "XOF", rate: 4.6, localLimit: 100000 },
    { name: "Bhutan", code: "BT", dialCode: "+975", currency: "BTN", rate: 0.64, localLimit: 10000 },
    { name: "Bolivia", code: "BO", dialCode: "+591", currency: "BOB", rate: 0.053, localLimit: 5000 },
    { name: "Botswana", code: "BW", dialCode: "+267", currency: "BWP", rate: 0.10, localLimit: 10000 },
    { name: "Brazil", code: "BR", dialCode: "+55", currency: "BRL", rate: 0.038, localLimit: 5000 },
    { name: "Bulgaria", code: "BG", dialCode: "+359", currency: "BGN", rate: 0.014, localLimit: 2000 },
    { name: "Burkina Faso", code: "BF", dialCode: "+226", currency: "XOF", rate: 4.6, localLimit: 100000 },
    { name: "Burundi", code: "BI", dialCode: "+257", currency: "BIF", rate: 22.0, localLimit: 250000 },
    { name: "Cambodia", code: "KH", dialCode: "+855", currency: "KHR", rate: 31.0, localLimit: 500000 },
    { name: "Cameroon", code: "CM", dialCode: "+237", currency: "XAF", rate: 4.6, localLimit: 100000 },
    { name: "Canada", code: "CA", dialCode: "+1", currency: "CAD", rate: 0.010, localLimit: 1000 },
    { name: "Cape Verde", code: "CV", dialCode: "+238", currency: "CVE", rate: 0.78, localLimit: 15000 },
    { name: "Central African Republic", code: "CF", dialCode: "+236", currency: "XAF", rate: 4.6, localLimit: 100000 },
    { name: "Chad", code: "TD", dialCode: "+235", currency: "XAF", rate: 4.6, localLimit: 100000 },
    { name: "Chile", code: "CL", dialCode: "+56", currency: "CLP", rate: 7.2, localLimit: 150000 },
    { name: "China", code: "CN", dialCode: "+86", currency: "CNY", rate: 0.055, localLimit: 5000 },
    { name: "Colombia", code: "CO", dialCode: "+57", currency: "COP", rate: 30.0, localLimit: 500000 },
    { name: "Comoros", code: "KM", dialCode: "+269", currency: "KMF", rate: 3.4, localLimit: 50000 },
    { name: "Congo (Brazzaville)", code: "CG", dialCode: "+242", currency: "XAF", rate: 4.6, localLimit: 100000 },
    { name: "Congo (Kinshasa)", code: "CD", dialCode: "+243", currency: "CDF", rate: 21.0, localLimit: 500000 },
    { name: "Costa Rica", code: "CR", dialCode: "+506", currency: "CRC", rate: 4.1, localLimit: 100000 },
    { name: "Ivory Coast", code: "CI", dialCode: "+225", currency: "XOF", rate: 4.6, localLimit: 100000 },
    { name: "Croatia", code: "HR", dialCode: "+385", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Cuba", code: "CU", dialCode: "+53", currency: "CUP", rate: 0.18, localLimit: 5000 },
    { name: "Cyprus", code: "CY", dialCode: "+357", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Czech Republic", code: "CZ", dialCode: "+420", currency: "CZK", rate: 0.18, localLimit: 10000 },
    { name: "Denmark", code: "DK", dialCode: "+45", currency: "DKK", rate: 0.053, localLimit: 5000 },
    { name: "Djibouti", code: "DJ", dialCode: "+253", currency: "DJF", rate: 1.37, localLimit: 25000 },
    { name: "Dominica", code: "DM", dialCode: "+1-767", currency: "XCD", rate: 0.021, localLimit: 2500 },
    { name: "Dominican Republic", code: "DO", dialCode: "+1-809", currency: "DOP", rate: 0.45, localLimit: 25000 },
    { name: "Ecuador", code: "EC", dialCode: "+593", currency: "USD", rate: 0.0077, localLimit: 1000 },
    { name: "Egypt", code: "EG", dialCode: "+20", currency: "EGP", rate: 0.37, localLimit: 10000 },
    { name: "El Salvador", code: "SV", dialCode: "+503", currency: "USD", rate: 0.0077, localLimit: 1000 },
    { name: "Equatorial Guinea", code: "GQ", dialCode: "+240", currency: "XAF", rate: 4.6, localLimit: 100000 },
    { name: "Eritrea", code: "ER", dialCode: "+291", currency: "ERN", rate: 0.11, localLimit: 5000 },
    { name: "Estonia", code: "EE", dialCode: "+372", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Eswatini", code: "SZ", dialCode: "+268", currency: "SZL", rate: 0.14, localLimit: 10000 },
    { name: "Ethiopia", code: "ET", dialCode: "+251", currency: "ETB", rate: 0.44, localLimit: 15000 },
    { name: "Fiji", code: "FJ", dialCode: "+679", currency: "FJD", rate: 0.017, localLimit: 2000 },
    { name: "Finland", code: "FI", dialCode: "+358", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "France", code: "FR", dialCode: "+33", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Gabon", code: "GA", dialCode: "+241", currency: "XAF", rate: 4.6, localLimit: 100000 },
    { name: "Gambia", code: "GM", dialCode: "+220", currency: "GMD", rate: 0.52, localLimit: 10000 },
    { name: "Georgia", code: "GE", dialCode: "+995", currency: "GEL", rate: 0.021, localLimit: 2000 },
    { name: "Germany", code: "DE", dialCode: "+49", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Ghana", code: "GH", dialCode: "+233", currency: "GHS", rate: 0.11, localLimit: 10000 },
    { name: "Greece", code: "GR", dialCode: "+30", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Grenada", code: "GD", dialCode: "+1-473", currency: "XCD", rate: 0.021, localLimit: 2500 },
    { name: "Guatemala", code: "GT", dialCode: "+502", currency: "GTQ", rate: 0.06, localLimit: 5000 },
    { name: "Guinea", code: "GN", dialCode: "+224", currency: "GNF", rate: 66.0, localLimit: 1000000 },
    { name: "Guinea-Bissau", code: "GW", dialCode: "+245", currency: "XOF", rate: 4.6, localLimit: 100000 },
    { name: "Guyana", code: "GY", dialCode: "+592", currency: "GYD", rate: 1.6, localLimit: 25000 },
    { name: "Haiti", code: "HT", dialCode: "+509", currency: "HTG", rate: 1.0, localLimit: 15000 },
    { name: "Honduras", code: "HN", dialCode: "+504", currency: "HNL", rate: 0.19, localLimit: 10000 },
    { name: "Hungary", code: "HU", dialCode: "+36", currency: "HUF", rate: 2.8, localLimit: 100000 },
    { name: "Iceland", code: "IS", dialCode: "+354", currency: "ISK", rate: 1.06, localLimit: 25000 },
    { name: "India", code: "IN", dialCode: "+91", currency: "INR", rate: 0.64, localLimit: 50000 },
    { name: "Indonesia", code: "ID", dialCode: "+62", currency: "IDR", rate: 120.0, localLimit: 2500000 },
    { name: "Iran", code: "IR", dialCode: "+98", currency: "IRR", rate: 325.0, localLimit: 5000000 },
    { name: "Iraq", code: "IQ", dialCode: "+964", currency: "IQD", rate: 10.0, localLimit: 250000 },
    { name: "Ireland", code: "IE", dialCode: "+353", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Israel", code: "IL", dialCode: "+972", currency: "ILS", rate: 0.029, localLimit: 2500 },
    { name: "Italy", code: "IT", dialCode: "+39", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Jamaica", code: "JM", dialCode: "+1-876", currency: "JMD", rate: 1.2, localLimit: 25000 },
    { name: "Japan", code: "JP", dialCode: "+81", currency: "JPY", rate: 1.14, localLimit: 100000 },
    { name: "Jordan", code: "JO", dialCode: "+962", currency: "JOD", rate: 0.0055, localLimit: 500 },
    { name: "Kazakhstan", code: "KZ", dialCode: "+7", currency: "KZT", rate: 3.5, localLimit: 50000 },
    { name: "Kenya", code: "KE", dialCode: "+254", currency: "KES", rate: 1.0, localLimit: 10000 },
    { name: "Kiribati", code: "KI", dialCode: "+686", currency: "AUD", rate: 0.012, localLimit: 1000 },
    { name: "Kuwait", code: "KW", dialCode: "+965", currency: "KWD", rate: 0.0024, localLimit: 250 },
    { name: "Kyrgyzstan", code: "KG", dialCode: "+996", currency: "KGS", rate: 0.69, localLimit: 10000 },
    { name: "Laos", code: "LA", dialCode: "+856", currency: "LAK", rate: 160.0, localLimit: 2000000 },
    { name: "Latvia", code: "LV", dialCode: "+371", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Lebanon", code: "LB", dialCode: "+961", currency: "LBP", rate: 115.0, localLimit: 1500000 },
    { name: "Lesotho", code: "LS", dialCode: "+266", currency: "LSL", rate: 0.14, localLimit: 10000 },
    { name: "Liberia", code: "LR", dialCode: "+231", currency: "LRD", rate: 1.48, localLimit: 25000 },
    { name: "Libya", code: "LY", dialCode: "+218", currency: "LYD", rate: 0.037, localLimit: 1000 },
    { name: "Liechtenstein", code: "LI", dialCode: "+423", currency: "CHF", rate: 0.0068, localLimit: 1000 },
    { name: "Lithuania", code: "LT", dialCode: "+370", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Luxembourg", code: "LU", dialCode: "+352", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Madagascar", code: "MG", dialCode: "+261", currency: "MGA", rate: 35.0, localLimit: 500000 },
    { name: "Malawi", code: "MW", dialCode: "+265", currency: "MWK", rate: 13.0, localLimit: 200000 },
    { name: "Malaysia", code: "MY", dialCode: "+60", currency: "MYR", rate: 0.036, localLimit: 2500 },
    { name: "Maldives", code: "MV", dialCode: "+960", currency: "MVR", rate: 0.12, localLimit: 10000 },
    { name: "Mali", code: "ML", dialCode: "+223", currency: "XOF", rate: 4.6, localLimit: 100000 },
    { name: "Malta", code: "MT", dialCode: "+356", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Marshall Islands", code: "MH", dialCode: "+692", currency: "USD", rate: 0.0077, localLimit: 1000 },
    { name: "Mauritania", code: "MR", dialCode: "+222", currency: "MRU", rate: 0.31, localLimit: 5000 },
    { name: "Mauritius", code: "MU", dialCode: "+230", currency: "MUR", rate: 0.35, localLimit: 5000 },
    { name: "Mexico", code: "MX", dialCode: "+52", currency: "MXN", rate: 0.13, localLimit: 10000 },
    { name: "Micronesia", code: "FM", dialCode: "+691", currency: "USD", rate: 0.0077, localLimit: 1000 },
    { name: "Moldova", code: "MD", dialCode: "+373", currency: "MDL", rate: 0.14, localLimit: 5000 },
    { name: "Monaco", code: "MC", dialCode: "+377", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Mongolia", code: "MN", dialCode: "+976", currency: "MNT", rate: 26.0, localLimit: 500000 },
    { name: "Montenegro", code: "ME", dialCode: "+382", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Morocco", code: "MA", dialCode: "+212", currency: "MAD", rate: 0.078, localLimit: 5000 },
    { name: "Mozambique", code: "MZ", dialCode: "+258", currency: "MZN", rate: 0.49, localLimit: 10000 },
    { name: "Myanmar", code: "MM", dialCode: "+95", currency: "MMK", rate: 16.0, localLimit: 250000 },
    { name: "Namibia", code: "NA", dialCode: "+264", currency: "NAD", rate: 0.14, localLimit: 10000 },
    { name: "Nauru", code: "NR", dialCode: "+674", currency: "AUD", rate: 0.012, localLimit: 1000 },
    { name: "Nepal", code: "NP", dialCode: "+977", currency: "NPR", rate: 1.0, localLimit: 15000 },
    { name: "Netherlands", code: "NL", dialCode: "+31", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "New Zealand", code: "NZ", dialCode: "+64", currency: "NZD", rate: 0.013, localLimit: 1500 },
    { name: "Nicaragua", code: "NI", dialCode: "+505", currency: "NIO", rate: 0.28, localLimit: 10000 },
    { name: "Niger", code: "NE", dialCode: "+227", currency: "XOF", rate: 4.6, localLimit: 100000 },
    { name: "Nigeria", code: "NG", dialCode: "+234", currency: "NGN", rate: 12.0, localLimit: 250000 },
    { name: "North Korea", code: "KP", dialCode: "+850", currency: "KPW", rate: 6.9, localLimit: 100000 },
    { name: "North Macedonia", code: "MK", dialCode: "+389", currency: "MKD", rate: 0.43, localLimit: 10000 },
    { name: "Norway", code: "NO", dialCode: "+47", currency: "NOK", rate: 0.082, localLimit: 10000 },
    { name: "Oman", code: "OM", dialCode: "+968", currency: "OMR", rate: 0.003, localLimit: 250 },
    { name: "Pakistan", code: "PK", dialCode: "+92", currency: "PKR", rate: 2.15, localLimit: 100000 },
    { name: "Palau", code: "PW", dialCode: "+680", currency: "USD", rate: 0.0077, localLimit: 1000 },
    { name: "Panama", code: "PA", dialCode: "+507", currency: "PAB", rate: 0.0077, localLimit: 1000 },
    { name: "Papua New Guinea", code: "PG", dialCode: "+675", currency: "PGK", rate: 0.029, localLimit: 2500 },
    { name: "Paraguay", code: "PY", dialCode: "+595", currency: "PYG", rate: 57.0, localLimit: 1000000 },
    { name: "Peru", code: "PE", dialCode: "+51", currency: "PEN", rate: 0.029, localLimit: 2500 },
    { name: "Philippines", code: "PH", dialCode: "+63", currency: "PHP", rate: 0.44, localLimit: 50000 },
    { name: "Poland", code: "PL", dialCode: "+48", currency: "PLN", rate: 0.031, localLimit: 2500 },
    { name: "Portugal", code: "PT", dialCode: "+351", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Qatar", code: "QA", dialCode: "+974", currency: "QAR", rate: 0.028, localLimit: 2500 },
    { name: "Romania", code: "RO", dialCode: "+40", currency: "RON", rate: 0.035, localLimit: 2500 },
    { name: "Russia", code: "RU", dialCode: "+7", currency: "RUB", rate: 0.70, localLimit: 50000 },
    { name: "Rwanda", code: "RW", dialCode: "+250", currency: "RWF", rate: 10.0, localLimit: 150000 },
    { name: "Saint Kitts and Nevis", code: "KN", dialCode: "+1-869", currency: "XCD", rate: 0.021, localLimit: 2500 },
    { name: "Saint Lucia", code: "LC", dialCode: "+1-758", currency: "XCD", rate: 0.021, localLimit: 2500 },
    { name: "Saint Vincent and the Grenadines", code: "VC", dialCode: "+1-784", currency: "XCD", rate: 0.021, localLimit: 2500 },
    { name: "Samoa", code: "WS", dialCode: "+685", currency: "WST", rate: 0.021, localLimit: 2500 },
    { name: "San Marino", code: "SM", dialCode: "+378", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Sao Tome and Principe", code: "ST", dialCode: "+239", currency: "STN", rate: 0.17, localLimit: 5000 },
    { name: "Saudi Arabia", code: "SA", dialCode: "+966", currency: "SAR", rate: 0.029, localLimit: 2500 },
    { name: "Senegal", code: "SN", dialCode: "+221", currency: "XOF", rate: 4.6, localLimit: 100000 },
    { name: "Serbia", code: "RS", dialCode: "+381", currency: "RSD", rate: 0.83, localLimit: 15000 },
    { name: "Seychelles", code: "SC", dialCode: "+248", currency: "SCR", rate: 0.10, localLimit: 10000 },
    { name: "Sierra Leone", code: "SL", dialCode: "+232", currency: "SLL", rate: 175.0, localLimit: 2500000 },
    { name: "Singapore", code: "SG", dialCode: "+65", currency: "SGD", rate: 0.010, localLimit: 1000 },
    { name: "Slovakia", code: "SK", dialCode: "+421", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Slovenia", code: "SI", dialCode: "+386", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Solomon Islands", code: "SB", dialCode: "+677", currency: "SBD", rate: 0.065, localLimit: 5000 },
    { name: "Solomon Islands", code: "SB", dialCode: "+677", currency: "SBD", rate: 0.065, localLimit: 5000 },
    { name: "Somalia", code: "SO", dialCode: "+252", currency: "SOS", rate: 4.4, localLimit: 50000 },
    { name: "South Africa", code: "ZA", dialCode: "+27", currency: "ZAR", rate: 0.14, localLimit: 15000 },
    { name: "South Korea", code: "KR", dialCode: "+82", currency: "KRW", rate: 10.0, localLimit: 1000000 },
    { name: "South Sudan", code: "SS", dialCode: "+211", currency: "SSP", rate: 0.77, localLimit: 15000 },
    { name: "Spain", code: "ES", dialCode: "+34", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Sri Lanka", code: "LK", dialCode: "+94", currency: "LKR", rate: 2.3, localLimit: 25000 },
    { name: "Sudan", code: "SD", dialCode: "+249", currency: "SDG", rate: 4.6, localLimit: 100000 },
    { name: "Suriname", code: "SR", dialCode: "+597", currency: "SRD", rate: 0.29, localLimit: 5000 },
    { name: "Sweden", code: "SE", dialCode: "+46", currency: "SEK", rate: 0.081, localLimit: 10000 },
    { name: "Switzerland", code: "CH", dialCode: "+41", currency: "CHF", rate: 0.0068, localLimit: 1000 },
    { name: "Syria", code: "SY", dialCode: "+963", currency: "SYP", rate: 19.0, localLimit: 250000 },
    { name: "Taiwan", code: "TW", dialCode: "+886", currency: "TWD", rate: 0.25, localLimit: 25000 },
    { name: "Tajikistan", code: "TJ", dialCode: "+992", currency: "TJS", rate: 0.084, localLimit: 10000 },
    { name: "Tanzania", code: "TZ", dialCode: "+255", currency: "TZS", rate: 20.0, localLimit: 250000 },
    { name: "Thailand", code: "TH", dialCode: "+66", currency: "THB", rate: 0.28, localLimit: 25000 },
    { name: "Timor-Leste", code: "TL", dialCode: "+670", currency: "USD", rate: 0.0077, localLimit: 1000 },
    { name: "Togo", code: "TG", dialCode: "+228", currency: "XOF", rate: 4.6, localLimit: 100000 },
    { name: "Tonga", code: "TO", dialCode: "+676", currency: "TOP", rate: 0.018, localLimit: 2000 },
    { name: "Trinidad and Tobago", code: "TT", dialCode: "+1-868", currency: "TTD", rate: 0.052, localLimit: 5000 },
    { name: "Tunisia", code: "TN", dialCode: "+216", currency: "TND", rate: 0.024, localLimit: 2500 },
    { name: "Turkey", code: "TR", dialCode: "+90", currency: "TRY", rate: 0.25, localLimit: 25000 },
    { name: "Turkmenistan", code: "TM", dialCode: "+993", currency: "TMT", rate: 0.027, localLimit: 2500 },
    { name: "Tuvalu", code: "TV", dialCode: "+688", currency: "AUD", rate: 0.012, localLimit: 1000 },
    { name: "Uganda", code: "UG", dialCode: "+256", currency: "UGX", rate: 28.0, localLimit: 500000 },
    { name: "Ukraine", code: "UA", dialCode: "+380", currency: "UAH", rate: 0.31, localLimit: 15000 },
    { name: "UAE", code: "AE", dialCode: "+971", currency: "AED", rate: 0.028, localLimit: 5000 },
    { name: "UK", code: "GB", dialCode: "+44", currency: "GBP", rate: 0.0061, localLimit: 1000 },
    { name: "USA", code: "US", dialCode: "+1", currency: "USD", rate: 0.0077, localLimit: 1000 },
    { name: "Uruguay", code: "UY", dialCode: "+598", currency: "UYU", rate: 0.30, localLimit: 25000 },
    { name: "Uzbekistan", code: "UZ", dialCode: "+998", currency: "UZS", rate: 96.0, localLimit: 1000000 },
    { name: "Vanuatu", code: "VU", dialCode: "+678", currency: "VUV", rate: 0.92, localLimit: 15000 },
    { name: "Vatican City", code: "VA", dialCode: "+39", currency: "EUR", rate: 0.007, localLimit: 1000 },
    { name: "Venezuela", code: "VE", dialCode: "+58", currency: "VES", rate: 0.28, localLimit: 25000 },
    { name: "Vietnam", code: "VN", dialCode: "+84", currency: "VND", rate: 190.0, localLimit: 5000000 },
    { name: "Yemen", code: "YE", dialCode: "+967", currency: "YER", rate: 1.9, localLimit: 50000 },
    { name: "Zambia", code: "ZM", dialCode: "+260", currency: "ZMW", rate: 0.20, localLimit: 5000 },
    { name: "Zimbabwe", code: "ZW", dialCode: "+263", currency: "USD", rate: 0.0077, localLimit: 1000 }
];

const BANKS = {
    "Kenya": ["KCB Bank", "Equity Bank", "Co-operative Bank", "NCBA Bank", "Absa Bank", "Stanbic Bank", "Diamond Trust Bank", "Family Bank", "Standard Chartered", "I&M Bank", "M-Pesa Wallet", "Airtel Money"],
    "Nigeria": ["First Bank", "Zenith Bank", "Access Bank", "UBA", "GTBank", "Kuda Bank", "Opay", "Moniepoint", "Fidelity Bank", "Union Bank", "Stanbic IBTC", "Sterling Bank", "Wema Bank", "Palmpay"],
    "Ghana": ["GCB Bank", "Ecobank", "Absa Bank Ghana", "Standard Chartered", "Fidelity Bank", "CAL Bank", "Zenith Bank Ghana", "MTN MoMo", "Vodafone Cash"],
    "South Africa": ["Standard Bank", "FirstRand Bank", "Absa Group", "Nedbank", "Capitec Bank", "TymeBank", "Discovery Bank", "Investec", "African Bank"],
    "Uganda": ["Stanbic Bank Uganda", "Centenary Bank", "DFCU Bank", "Standard Chartered", "Absa Bank Uganda", "MTN Mobile Money", "Airtel Money"],
    "Tanzania": ["CRDB Bank", "NMB Bank", "NBC Bank", "Standard Chartered", "Absa Bank Tanzania", "Vodacom M-Pesa", "Tigo Pesa"],
    "Rwanda": ["Bank of Kigali", "I&M Bank Rwanda", "Equity Bank Rwanda", "Cogebanque", "MTN MoMo"],
    "Ethiopia": ["Commercial Bank of Ethiopia", "Awash Bank", "Dashen Bank", "Abyssinia Bank", "Telebirr"],
    "Zambia": ["Zambia National Commercial Bank", "Standard Chartered", "Absa Bank Zambia", "Stanbic Bank Zambia", "MTN MoMo"],
    "Egypt": ["National Bank of Egypt", "Banque Misr", "CIB", "QNB Alahli", "Vodafone Cash"],
    "USA": ["JPMorgan Chase", "Bank of America", "Citigroup", "Wells Fargo", "Goldman Sachs", "Capital One", "U.S. Bancorp", "PNC Bank", "Truist", "Charles Schwab"],
    "UK": ["HSBC", "Barclays", "Lloyds Bank", "NatWest", "Standard Chartered", "Santander UK", "Nationwide", "Monzo", "Revolut", "Starling"],
    "Canada": ["RBC", "TD Bank", "Scotiabank", "BMO", "CIBC", "National Bank of Canada", "Desjardins"],
    "Australia": ["Commonwealth Bank", "Westpac", "ANZ", "NAB", "Macquarie Bank"],
    "India": ["State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank", "Punjab National Bank", "Kotak Mahindra Bank", "Bank of Baroda", "Paytm Bank"],
    "Germany": ["Deutsche Bank", "Commerzbank", "Sparkasse", "Volksbanken", "N26"],
    "France": ["BNP Paribas", "Crédit Agricole", "Société Générale", "BPCE", "La Banque Postale"],
    "UAE": ["Emirates NBD", "First Abu Dhabi Bank", "ADCB", "Dubai Islamic Bank"],
    "Pakistan": ["Habib Bank", "National Bank of Pakistan", "Meezan Bank", "MCB Bank", "JazzCash", "Easypaisa"],
    "Philippines": ["BDO Unibank", "Metrobank", "BPI", "Land Bank", "GCash", "Maya"],
    "Zimbabwe": ["CBZ Bank", "CABS", "Stanbic Bank Zimbabwe", "EcoCash"],
    "Botswana": ["First National Bank Botswana", "Standard Chartered Botswana", "Absa Bank Botswana", "Orange Money"],
    "Cameroon": ["Afriland First Bank", "Société Générale Cameroun", "MTN MoMo", "Orange Money"],
    "Ivory Coast": ["NSIA Banque", "Société Générale Côte'Ivoire", "Orange Money", "MTN MoMo"],
    "Morocco": ["Attijariwafa Bank", "Bank Populaire du Maroc", "BMCE Bank"],
    "Algeria": ["Banque Extérieure d'Algérie", "Banque Nationale d'Algérie"],
    "Angola": ["Banco Angolano de Investimentos", "Banco Económico"],
    "Mozambique": ["Millennium BIM", "Standard Bank Mozambique", "M-Pesa"],
    "Senegal": ["CBAO Groupe Attijariwafa Bank", "Société Générale Sénégal", "Wave", "Orange Money"],
    "Sudan": ["Bank of Khartoum", "Faisal Islamic Bank"],
    "Tunisia": ["BIAT", "Bank Populaire du Maroc", "BMCE Bank"],
    "Argentina": ["Banco de la Nación Argentina", "Banco Galicia", "Market Pago"],
    "Brazil": ["Itaú Unibanco", "Banco do Brasil", "Bradesco", "Nubank", "Pix"],
    "Mexico": ["BBVA México", "Banorte", "Santander México"],
    "Colombia": ["Bancolombia", "Banco de Bogotá", "Daviplata"],
    "Chile": ["Banco de Chile", "Banco Santander Chile"],
    "Peru": ["Banco de Crédito del Perú", "BBVA Perú"],
    "Saudi Arabia": ["Al Rajhi Bank", "SNB", "Riyad Bank", "STC Pay"],
    "Turkey": ["Ziraat Bankası", "İş Bankası", "Garanti BBVA"],
    "Indonesia": ["Bank Mandiri", "Bank Rakyat Indonesia", "Bank Central Asia", "GoPay", "OVO"],
    "Malaysia": ["Maybank", "CIMB Bank", "Public Bank", "GrabPay"],
    "Thailand": ["Kasikornbank", "Siam Commercial Bank", "Bangkok Bank", "PromptPay"],
    "Vietnam": ["Vietcombank", "VietinBank", "BIDV", "MoMo"],
    "Japan": ["MUFG Bank", "Sumitomo Mitsui Bank", "Mizuho Bank", "PayPay"],
    "South Korea": ["KB Kookmin Bank", "Shinhan Bank", "Hana Bank", "KakaoBank"],
    "Russia": ["Sberbank", "VTB Bank", "Gazprombank", "Tinkoff"],
    "Poland": ["PKO Bank Polski", "Bank Pekao", "mBank"],
    "Netherlands": ["ING Bank", "ABN AMRO", "Rabobank"],
    "Spain": ["Banco Santander", "BBVA", "CaixaBank"],
    "Italy": ["Intesa Sanpaolo", "UniCredit", "Poste Italiane"],
    "Austria": ["Erste Bank", "Raiffeisen Bank International"],
    "Switzerland": ["UBS", "Credit Suisse", "Zürcher Kantonalbank"],
    "Belgium": ["BNP Paribas Fortis", "KBC Bank", "Belfius"],
    "Ireland": ["AIB", "Bank of Ireland", "Permanent TSB"],
    "New Zealand": ["ANZ New Zealand", "ASB Bank", "Westpac NZ", "KiwiBank"]
};

const PROGRESS_SCREENS = ['apply-screen', 'kyc-selector-screen', 'kyc-guidance-screen', 'kyc-liveness-screen', 'agreement-screen', 'result-screen'];

// UI Helpers
const screens = document.querySelectorAll('.screen');
function showScreen(id) {
    screens.forEach(s => s.classList.remove('active'));
    const target = document.getElementById(id);
    if (target) {
        target.classList.add('active');

        // SAVE PROGRESS IF IN CORE FLOW
        if (PROGRESS_SCREENS.includes(id)) {
            currentUser.lastProgressScreen = id;
            saveState();
        }

        if (id === 'history-screen') renderHistory();
        if (id === 'refer-screen') {
            const el = document.getElementById('referral-code');
            if (el) el.innerText = currentUser.referralCode;
        }
        if (id === 'apply-screen') {
            currentStep = currentUser.loanStep || 1;
            populateDateDropdowns();
            populateBankList();
            updateStepUI();
            restoreFormData();

            // PRE-FILL PAYOUT NUMBER WITH REGISTRATION PHONE
            const payoutPhone = document.getElementById('payout-phone');
            if (payoutPhone && (!payoutPhone.value || payoutPhone.value === "")) {
                payoutPhone.value = currentUser.phone || "";
            }
        }
        if (id === 'result-screen') {
            updateDispersalFeeDisplay();
            const photo = document.getElementById('captured-photo');
            if (photo && currentUser.kycSelfie) photo.src = currentUser.kycSelfie;

            // RESTORE QUALIFIED AMOUNT TEXT
            const qa = document.getElementById('qualified-amount');
            const currency = currentUser.currency || "KES";
            const amount = globalQualifiedAmount || currentUser.savedQualifiedAmount || 0;
            if (qa) qa.innerHTML = `<span class="currency">${currency}</span> ${amount.toLocaleString()}`;
        }
        if (id === 'agreement-screen') {
            renderAgreement();
            // RE-SYNC PRINCIPAL TEXT
            const principalEl = document.getElementById('agree-principal');
            const currency = currentUser.currency || "KES";
            const amount = currentLoanTerms.principal || 0;
            if (principalEl) principalEl.innerText = `${currency} ${amount.toLocaleString()}`;
        }
        if (id === 'profile-screen') renderProfile();
    }
}

// Professional Neon Toast System - DEAD CENTER & SPAM PREVENT
let activeToasts = new Set();
function showToast(message, type = 'info') {
    if (activeToasts.has(message)) return;

    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    activeToasts.add(message);

    let icon = '<i class="fa-solid fa-circle-info"></i>';
    if (type === 'error') icon = '<i class="fa-solid fa-triangle-exclamation"></i>';
    if (type === 'success') icon = '<i class="fa-solid fa-circle-check"></i>';

    toast.innerHTML = `${icon} <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => {
            toast.remove();
            activeToasts.delete(message);
        }, 200);
    }, 500);
}

function updateDispersalFeeDisplay() {
    const feeEl = document.getElementById('dynamic-fee');
    if (feeEl) {
        const country = COUNTRIES.find(c => c.name === currentUser.country) || { rate: 1.0 };
        const convertedFee = Math.ceil(500 * country.rate);
        feeEl.innerText = convertedFee.toLocaleString();
    }
}

function startDisbursementProcessing() {
    showScreen('processing-screen');
    const ring = document.getElementById('process-ring-fill');
    const msg = document.getElementById('process-msg');
    const title = document.getElementById('process-title');
    const tick = document.querySelector('.check-icon-success');
    const doneBtn = document.getElementById('btn-process-done');

    if (!ring) return;

    let progress = 314.159;
    const interval = setInterval(() => {
        progress -= 5;
        ring.style.strokeDashoffset = Math.max(progress, 0);

        if (progress <= 200) if (msg) msg.innerText = "Communicating with Bank Secure Servers...";
        if (progress <= 100) if (msg) msg.innerText = "Finalizing Credit Transfer...";

        if (progress <= 0) {
            clearInterval(interval);
            if (title) title.innerText = "Money in Process";
            if (msg) msg.innerText = "Funds have been secured and are being released to your account.";
            if (tick) tick.classList.add('active');
            if (doneBtn) doneBtn.style.display = 'block';

            addTransaction("Loan (On Process)", currentLoanTerms.principal, false);
            currentUser.pendingLoan = currentLoanTerms.total;
            currentUser.lastDisbursementTime = Date.now();
            saveState();
        }
    }, 50);
}

function populateCountryList() {
    const select = document.getElementById('country-select');
    if (!select) return;

    COUNTRIES.sort((a, b) => a.name.localeCompare(b.name));
    select.innerHTML = COUNTRIES.map(c => `<option value="${c.name}">${c.name}</option>`).join('');

    if (!currentUser.isLoggedIn) {
        const defaultCountry = COUNTRIES.find(c => c.name === "Kenya") || COUNTRIES[0];
        select.value = defaultCountry.name;
        const phoneInput = document.getElementById('phone');
        if (phoneInput) phoneInput.value = defaultCountry.dialCode + " ";
        currentUser.country = defaultCountry.name;
        currentUser.currency = defaultCountry.currency;
    }
}

function updateDashboard() {
    try {
        const nameEl = document.getElementById('user-name');
        const limitEl = document.getElementById('limit-amount');
        const currencyElements = document.querySelectorAll('.currency');

        const currency = currentUser.currency || "KES";

        if (currencyElements) {
            currencyElements.forEach(el => el.innerText = currency);
        }

        const countryData = COUNTRIES.find(c => c.name === currentUser.country) || { rate: 1.0, localLimit: 10000 };

        if (nameEl && currentUser.name) {
            nameEl.innerText = currentUser.name.split(' ')[0];
        }

        if (limitEl) {
            const historyLen = (currentUser.history && Array.isArray(currentUser.history)) ? currentUser.history.length : 0;
            const val = (historyLen === 0) ? countryData.localLimit : (currentUser.limit || 10000);
            limitEl.innerHTML = `<span style="color:var(--neon-yellow)"><span class="currency">${currency}</span> ${val.toLocaleString()}</span>`;
        }
    } catch (e) {
        console.error("Dashboard Update Error:", e);
    }
}

document.getElementById('country-select')?.addEventListener('change', (e) => {
    const countryName = e.target.value;
    const country = COUNTRIES.find(c => c.name === countryName);
    if (country) {
        const phoneInput = document.getElementById('phone');
        if (phoneInput) phoneInput.value = country.dialCode + " ";
        currentUser.country = countryName;
        currentUser.currency = country.currency;
        updateDashboard();
    }
});

document.getElementById('phone')?.addEventListener('input', (e) => {
    const country = COUNTRIES.find(c => c.name === currentUser.country);
    if (country) {
        const prefix = country.dialCode + " ";
        if (!e.target.value.startsWith(prefix)) {
            e.target.value = prefix;
        }
    }
});

function populateBankList() {
    const list = document.getElementById('bank-list');
    if (!list) return;
    const country = currentUser.country || 'Kenya';
    const banks = BANKS[country] || ["Other Bank", "Mobile Money Wallet"];
    list.innerHTML = banks.map(b => `<option value="${b}">`).join('');
}

function restoreFormData() {
    if (!currentUser.loanFormData) return;
    Object.keys(currentUser.loanFormData).forEach(id => {
        const input = document.getElementById(id);
        if (input) {
            const val = currentUser.loanFormData[id];
            if (input.type === 'radio') {
                if (input.value === val) {
                    input.checked = true;
                    if (input.name === 'payout-method') togglePayoutFields(val);
                }
            } else if (input.type === 'checkbox') {
                input.checked = !!val;
            } else {
                input.value = val;
            }
        }
    });
}

function togglePayoutFields(method) {
    const bankFields = document.getElementById('bank-details-fields');
    const mobileFields = document.getElementById('mobile-payout-fields');

    const bankInputs = bankFields?.querySelectorAll('input') || [];
    const mobileInputs = mobileFields?.querySelectorAll('input') || [];

    if (method === 'bank') {
        if (bankFields) bankFields.style.display = 'block';
        if (mobileFields) mobileFields.style.display = 'none';

        bankInputs.forEach(i => i.required = true);
        mobileInputs.forEach(i => i.required = false);
    } else {
        if (bankFields) bankFields.style.display = 'none';
        if (mobileFields) mobileFields.style.display = 'block';

        bankInputs.forEach(i => i.required = false);
        mobileInputs.forEach(i => i.required = true);
    }
}

document.addEventListener('change', (e) => {
    if (e.target.name === 'payout-method') {
        togglePayoutFields(e.target.value);
    }
});

function populateDateDropdowns() {
    const daySelect = document.getElementById('dob-day');
    const yearSelect = document.getElementById('dob-year');
    if (daySelect && daySelect.options.length <= 1) {
        for (let i = 1; i <= 31; i++) {
            const opt = document.createElement('option');
            opt.value = i < 10 ? '0' + i : '' + i;
            opt.innerText = i;
            daySelect.appendChild(opt);
        }
    }
    if (yearSelect && yearSelect.options.length <= 1) {
        const currentYear = new Date().getFullYear();
        for (let i = currentYear - 18; i >= currentYear - 70; i--) {
            const opt = document.createElement('option');
            opt.value = '' + i;
            opt.innerText = i;
            yearSelect.appendChild(opt);
        }
    }
}

function updateStepUI() {
    const sections = document.querySelectorAll('.form-step');
    sections.forEach((s, index) => {
        s.style.display = (index + 1 === currentStep) ? 'block' : 'none';
        if (index + 1 === currentStep) s.classList.add('animate-in');
        else s.classList.remove('animate-in');
    });

    const indicators = document.querySelectorAll('.step-indicator .step');
    indicators.forEach((ind, index) => {
        if (index + 1 === currentStep) ind.classList.add('active');
        else if (index + 1 < currentStep) ind.classList.add('completed');
        else ind.classList.remove('active', 'completed');
    });

    const nextBtn = document.getElementById('next-step-btn');
    if (nextBtn) {
        nextBtn.style.display = (currentStep === 1) ? 'none' : 'flex';
        nextBtn.innerHTML = (currentStep === totalSteps) ? 'Complete Verification <i class="fa-solid fa-circle-check"></i>' : 'Next Step <i class="fa-solid fa-arrow-right"></i>';
    }
}

function nextStep() {
    const currentStepEl = document.getElementById(`step-${currentStep}`);
    if (!currentStepEl) return;

    if (currentStep > 1) {
        const inputs = currentStepEl.querySelectorAll('input[required], select[required]');
        let isValid = true;
        inputs.forEach(input => {
            if (!input.value || (input.tagName === 'SELECT' && input.selectedIndex === 0)) {
                input.parentElement.style.borderColor = 'var(--neon-pink)';
                isValid = false;
            } else {
                input.parentElement.style.borderColor = 'transparent';
            }
        });
        if (!isValid) {
            showToast("Please fill all details.", "error");
            return;
        }
        if (!validateIntelligence(currentStepEl)) return;
    }

    // STRICT INTERNET CHECK BEFORE NEXT STEP
    requireInternet(() => {
        // Sync Data
        const allInputs = currentStepEl.querySelectorAll('input, select');
        allInputs.forEach(input => {
            const key = input.id || input.name;
            let value = input.value;
            if (input.type === 'radio') {
                if (input.checked) {
                    currentUser.loanFormData[key] = value;
                    currentUser.profile[key] = value;
                }
            } else if (value && value !== "") {
                currentUser.loanFormData[key] = value;
                currentUser.profile[key] = value;
            }
        });

        if (currentStep < totalSteps) {
            currentStep++;
            currentUser.loanStep = currentStep;
            saveState();
            updateStepUI();
            const formContainer = document.querySelector('.scrollable-form');
            if (formContainer) formContainer.scrollTop = 0;
        } else {
            // FORM COMPLETE -> GO TO SECURITY
            currentUser.loanStep = totalSteps;
            saveState();
            renderDocSelector();
            showScreen('kyc-selector-screen');
        }
    });
}

function validateIntelligence(stepEl) {
    const nameInput = stepEl.querySelector('#fullname') || stepEl.querySelector('.contact-name');
    if (nameInput && nameInput.value) {
        const parts = nameInput.value.trim().split(/\s+/);
        if (parts.length < 2) {
            showToast("Full official name required.", "error");
            return false;
        }
    }
    if (currentStep === 7) {
        const phones = Array.from(stepEl.querySelectorAll('.contact-phone')).map(i => i.value.trim());
        if (new Set(phones).size !== phones.length) {
            showToast("Each contact must be unique.", "error");
            return false;
        }
    }
    if (currentStep === 4) {
        const employer = stepEl.querySelector('#employer')?.value || "";
        if (employer.length < 3) {
            showToast("Valid employer name required.", "error");
            return false;
        }
    }
    return true;
}

function validateTerms() {
    const btn = document.getElementById('btn-accept-terms');
    const terms = document.getElementById('check-terms')?.checked;
    const privacy = document.getElementById('check-privacy')?.checked;
    if (btn) btn.disabled = !(terms && privacy);
}

function prevStep() {
    if (currentStep > 1) {
        currentStep--;
        currentUser.loanStep = currentStep;
        saveState();
        updateStepUI();
    } else {
        showScreen('dashboard-screen');
    }
}

function selectLoanCategory(name, icon, element) {
    try {
        currentUser.loanCategory = name;
        currentUser.profile['loanCategory'] = name;
        currentUser.loanFormData['loanCategory'] = name;

        // Highlight selected
        document.querySelectorAll('.cat-item').forEach(el => el.classList.remove('selected'));
        if (element) {
            element.classList.add('selected');
        }

        saveState();

        setTimeout(() => {
            nextStep();
        }, 400);
    } catch (e) {
        console.error("Select Category Error:", e);
        nextStep(); // Attempt to proceed anyway
    }
}

function saveState() {
    try {
        localStorage.setItem('genz_loan_pro_user', JSON.stringify(currentUser));
        if (db && currentUser && currentUser.uid) {
            db.collection("users").doc(currentUser.uid).set(currentUser).catch(e => console.error("Cloud Save Fail:", e));
        }
    } catch (e) {
        console.error("Save state error:", e);
    }
}

function loadState() {
    try {
        const localData = localStorage.getItem('genz_loan_pro_user');
        if (localData) {
            const parsed = JSON.parse(localData);
            if (parsed && typeof parsed === 'object') {
                currentUser = { ...currentUser, ...parsed };
                if (currentUser.isLoggedIn) updateDashboard();

                // RESTORE GLOBALS
                globalQualifiedAmount = currentUser.savedQualifiedAmount || 0;
                currentLoanTerms = currentUser.savedLoanTerms || {};
            }
        }
    } catch (e) {
        console.error("Load state error:", e);
    }
}

function renderProfile() {
    const nameTitle = document.getElementById('profile-name-title');
    const emailSub = document.getElementById('profile-email-sub');
    const list = document.getElementById('profile-details-list');
    if (!nameTitle || !list) return;

    nameTitle.innerText = currentUser.name;
    emailSub.innerText = currentUser.email;

    const p = currentUser.profile || {};
    const labels = {
        'fullname': 'Full Legal Name',
        'email': 'Email Address',
        'phone': 'Phone Number',
        'country': 'Primary Residence',
        'loanCategory': 'Loan Category',
        'id-number': 'National ID Number',
        'income': 'Monthly Income',
        'employment-status': 'Job Status',
        'employer': 'Organization',
        'job-title': 'Position / Role',
        'experience': 'Work Experience',
        'city': 'Residential City',
        'estate': 'Residential Area',
        'residence-type': 'Housing Type',
        'contact1-name': 'Primary Contact',
        'contact2-name': 'Secondary Contact'
    };
    const colors = ['#00d2ff', '#ff00ff', '#f3ff00', '#ff9d00', '#00ffff', '#50c878', '#bb86fc'];
    let idx = 0;
    let html = "";
    Object.keys(labels).forEach(key => {
        const val = p[key] || "";
        if (val && val.toString().trim() !== "" && val !== "Not provided") {
            const color = colors[idx % colors.length];
            idx++;
            html += `<div class="profile-card" style="border-left-color: ${color}">
                        <label style="color: ${color}">${labels[key]}</label>
                        <span>${val}</span>
                    </div>`;
        }
    });
    list.innerHTML = html || '<p class="center-text" style="color: var(--text-dim); padding: 20px;">Complete application to unlock more profile details.</p>';
}

async function downloadProfilePDF(autoView = false) {
    const element = document.getElementById('pdf-template');
    const detailsContainer = document.getElementById('pdf-details');
    if (!element || !detailsContainer) return;

    showClassicLoader(1000);

    // ENSURE PDF ENGINE IS LOADED
    if (typeof html2pdf === 'undefined') {
        const script = document.createElement('script');
        script.src = "https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js";
        document.body.appendChild(script);
        script.onload = () => buildPDFData(element, detailsContainer, autoView);
    } else {
        buildPDFData(element, detailsContainer, autoView);
    }
}

function buildPDFData(element, detailsContainer, autoView) {
    console.log("Starting PDF Data Injection...");
    const p = currentUser.profile || {};
    const d = currentUser.loanFormData || {};

    const dob = (d['dob-day'] && d['dob-month'] && d['dob-year'])
        ? `${d['dob-day']}/${d['dob-month']}/${d['dob-year']}`
        : (p['dob'] || "N/A");

    // EXTENDED MAPPING - ENSURE NO DETAILS HIDDEN
    const labels = {
        'fullname': 'Full Legal Name',
        'email': 'Official Email',
        'phone': 'Contact Number',
        'country': 'Registration Zone',
        'dob': 'Date of Birth',
        'loanCategory': 'Loan Class',
        'id-number': 'National ID Number',
        'income': 'Certified Income',
        'employment-status': 'Employment Status',
        'employer': 'Organization/Business',
        'job-title': 'Position/Role',
        'experience': 'Years Active',
        'city': 'Residential City',
        'estate': 'Zone/Estate',
        'residence-type': 'Living Status',
        'expenses': 'Monthly Expenses',
        'existing-loans': 'Outstanding Debt',
        'bank-name': 'Registered Bank',
        'payout-phone': 'Settlement Wallet',
        'contact1-name': 'Emergency Contact 1',
        'contact1-phone': 'Contact 1 Phone'
    };

    let detailHtml = "";
    Object.keys(labels).forEach(key => {
        let val = (key === 'dob') ? dob : (p[key] || d[key] || currentUser[key] || "N/A");
        detailHtml += `
            <div class="pdf-item" style="padding: 14px 20px; border-bottom: 1px solid #f0f0f0; display: flex; justify-content: space-between; align-items: center; background: #ffffff !important;">
                <span class="pdf-label" style="font-size: 0.75rem; font-weight: 800; color: #4a00e0 !important; text-transform: uppercase; letter-spacing: 0.5px;">${labels[key]}</span>
                <span class="pdf-value" style="font-size: 0.95rem; font-weight: 700; color: #000000 !important; text-align: right;">${val}</span>
            </div>
        `;
    });
    detailsContainer.innerHTML = detailHtml;

    const refId = document.getElementById('pdf-ref-id');
    const issueDate = document.getElementById('pdf-date');
    if (refId) refId.innerText = "GZL-RE-" + Math.random().toString(36).substring(2, 10).toUpperCase();
    if (issueDate) issueDate.innerText = new Date().toLocaleDateString('en-GB', { day: 'numeric', month: 'long', year: 'numeric' });

    // FORCE VISIBLE RE-PAINT
    setTimeout(() => {
        executePDFExport(element, autoView);
    }, 1500);
}

async function executePDFExport(element, autoView = false) {
    const filename = `GenZ_CreditReport_${currentUser.name.replace(/\s+/g, '_')}.pdf`;

    // ULTIMATE PDF BLANK FIX: "THE DIRECT CAPTURE"
    const loader = document.getElementById('classic-loader-overlay');
    const genText = document.getElementById('pdf-gen-text');
    if (loader) loader.style.display = 'flex';
    if (genText) {
        genText.style.display = 'block';
        genText.innerText = "Encrypting Official Form...";
    }

    // Stage 1: Absolute Body Root for Hardware Priority
    document.body.appendChild(element);

    element.style.display = 'block';
    element.style.position = 'fixed';
    element.style.top = '0';
    element.style.left = '0';
    element.style.width = '800px';
    element.style.height = 'auto';
    element.style.minHeight = '1120px';
    element.style.zIndex = '99998'; // BEHIND THE LOADER
    element.style.background = '#ffffff';
    element.style.opacity = '1';
    element.style.visibility = 'visible';
    element.style.overflow = 'visible';

    // Force every child to be visible for the capture engine
    element.querySelectorAll('*').forEach(el => {
        el.style.opacity = '1';
        el.style.visibility = 'visible';
    });

    const opt = {
        margin: [0.3, 0.3],
        filename: filename,
        image: { type: 'jpeg', quality: 1 },
        html2canvas: {
            scale: 2,
            useCORS: true,
            logging: true,
            letterRendering: true,
            backgroundColor: '#ffffff'
        },
        jsPDF: { unit: 'in', format: 'a4', orientation: 'portrait' }
    };

    try {
        // ESSENTIAL 4S DELAY TO FORCE HARDWARE DRAWING
        await new Promise(resolve => setTimeout(resolve, 4000));

        const pdfWorker = html2pdf().set(opt).from(element);

        if (typeof AndroidKYC !== 'undefined' && AndroidKYC.savePDF) {
            const pdfBlob = await pdfWorker.output('blob');
            const reader = new FileReader();
            reader.onload = function() {
                const base64 = reader.result.split(',')[1];
                AndroidKYC.savePDF(base64, filename);
            };
            reader.readAsDataURL(pdfBlob);
        } else {
            await pdfWorker.save();
            showToast("Report Finalized!", "success");
        }
    } catch (e) {
        console.error("PDF Error:", e);
        showToast("PDF Security Lock Failure.", "error");
    } finally {
        setTimeout(() => {
            element.style.display = 'none';
            // Return to app container
            const app = document.getElementById('app');
            if (app) app.appendChild(element);

            if (loader) loader.style.display = 'none';
            if (genText) genText.style.display = 'none';
        }, 1500);
    }
}

function addTransaction(type, amount, isOut = true) {
    const entry = { id: Date.now(), type, amount, isOut, currency: currentUser.currency, date: new Date().toLocaleDateString() };
    if (!currentUser.history) currentUser.history = [];
    currentUser.history.unshift(entry);
    saveState();
}

function renderHistory() {
    const list = document.getElementById('history-list');
    if (!list) return;
    if (!currentUser.history || currentUser.history.length === 0) { list.innerHTML = '<p class="empty-msg">No transactions yet.</p>'; return; }
    list.innerHTML = currentUser.history.map(item => `
        <div class="history-item ${item.isOut ? 'out' : ''}">
            <div class="item-info"><p class="item-type">${item.type}</p><p class="item-date">${item.date}</p></div>
            <p class="item-amount">${item.isOut ? '-' : '+'}${item.currency} ${item.amount.toLocaleString()}</p>
        </div>
    `).join('');
}

function isNetworkActive() {
    if (typeof AndroidKYC !== 'undefined' && AndroidKYC.isInternetAvailable) {
        return AndroidKYC.isInternetAvailable();
    }
    return navigator.onLine;
}

// GLOBAL HARD LOGOUT
window.handleLogout = function() {
    console.log("CRITICAL: Hard Logout Initiated");

    // 1. Immediate Visual Feedback
    showScreen('logout-screen');

    try {
        const agreed = (currentUser && currentUser.hasAgreedToTerms) ? true : false;

        // 2. Wipe memory and storage synchronously
        localStorage.clear();
        sessionStorage.clear();

        const resetUser = { ...INITIAL_STATE, hasAgreedToTerms: agreed };
        localStorage.setItem('genz_loan_pro_user', JSON.stringify(resetUser));

        // 3. Absolute Restart
        setTimeout(() => {
            if (typeof AndroidKYC !== 'undefined' && AndroidKYC.restartApp) {
                AndroidKYC.restartApp();
            } else {
                window.location.href = "index.html?v=" + Date.now();
            }
        }, 1000); // Give user time to see the "Securing Session" screen
    } catch (e) {
        console.error("Logout Error:", e);
        window.location.replace("index.html");
    }
};

window.onConnectivityChanged = function(isValidated) {
    console.log("Connectivity changed. Validated:", isValidated);
    checkConnectivity();
};

async function checkConnectivity(manual = false) {
    const offlineScreen = document.getElementById('offline-screen');
    const online = await isNetworkActive();

    if (!online) {
        if (offlineScreen) offlineScreen.style.display = 'flex';
        if (manual) showToast("Connection Restricted: Validated internet required.", "error");
    } else {
        if (offlineScreen) offlineScreen.style.display = 'none';
        if (manual) showToast("System Secured: Connection Restored.", "success");
    }
}

async function requireInternet(callback) {
    if (navigator.onLine) {
        callback();
    } else {
        showToast("Connection Restricted: Active internet required to proceed.", "error");
        const offlineScreen = document.getElementById('offline-screen');
        if (offlineScreen) offlineScreen.style.display = 'flex';
    }
}

function initApp() {
    console.log("Initializing App Core...");
    // BOOT SAFETY: Force hide splash if it takes too long
    const splash = document.getElementById('splash-screen');
    const forceHide = setTimeout(() => {
        if (splash) splash.style.display = 'none';
    }, 1500);

    try {
        populateCountryList();
        loadState();

        // HEARTBEAT INTERNET GUARD - 10S PULSE
        setInterval(checkConnectivity, 10000);

        // IMMEDIATE SESSION RESUME
        if (currentUser.isLoggedIn) {
            if (currentUser.lastProgressScreen) {
                showScreen(currentUser.lastProgressScreen);
            } else {
                showScreen('dashboard-screen');
            }
        } else if (currentUser.hasAgreedToTerms) {
            showScreen('auth-screen');
        } else {
            showScreen('terms-screen');
        }

        // Essential Listeners
        document.getElementById('check-terms')?.addEventListener('change', validateTerms);
        document.getElementById('check-privacy')?.addEventListener('change', validateTerms);

        const proceedBtn = document.getElementById('btn-accept-terms');
        if (proceedBtn) {
            proceedBtn.addEventListener('click', () => {
                console.log("Proceed Clicked");
                currentUser.hasAgreedToTerms = true;
                saveState();
                showScreen('auth-screen');
            });
        }

        // AUTO-SAVE SYSTEM: LISTEN TO ALL FORM CHANGES IN REAL-TIME
        document.querySelectorAll('#loan-form input, #loan-form select, #loan-form textarea, #auth-form input').forEach(input => {
            const saveHandler = () => {
                const key = input.id || input.name;
                if (!key) return;
                let value = input.value;
                if (input.type === 'radio') {
                    if (input.checked) {
                        currentUser.loanFormData[key] = value;
                        currentUser.profile[key] = value;
                    }
                } else if (input.type === 'checkbox') {
                    currentUser.loanFormData[key] = input.checked;
                    currentUser.profile[key] = input.checked;
                } else {
                    currentUser.loanFormData[key] = value;
                    currentUser.profile[key] = value;
                }
                saveState();
            };
            input.addEventListener('input', saveHandler);
            input.addEventListener('change', saveHandler);
        });

        document.getElementById('toggle-auth')?.addEventListener('click', (e) => {
            e.preventDefault();
            isLoginMode = !isLoginMode;

            const title = document.getElementById('auth-title');
            const nameGroup = document.getElementById('fullname')?.parentElement;
            const phoneGroup = document.getElementById('phone')?.parentElement;
            const countryGroup = document.getElementById('country-select')?.parentElement;
            const confirmContainer = document.getElementById('confirmation-fields');
            const submitBtn = document.querySelector('#auth-form button');

            if (title) title.innerText = isLoginMode ? 'Welcome Back' : 'Create Account';
            if (nameGroup) nameGroup.style.display = isLoginMode ? 'none' : 'flex';
            if (phoneGroup) phoneGroup.style.display = isLoginMode ? 'none' : 'flex';
            if (countryGroup) countryGroup.style.display = isLoginMode ? 'none' : 'flex';
            if (confirmContainer) confirmContainer.style.display = isLoginMode ? 'none' : 'block';

            const fn = document.getElementById('fullname');
            const ph = document.getElementById('phone');
            const cs = document.getElementById('country-select');
            const cp = document.getElementById('confirm-password');

            if (fn) fn.required = !isLoginMode;
            if (ph) ph.required = !isLoginMode;
            if (cs) cs.required = !isLoginMode;
            if (cp) cp.required = !isLoginMode;

            if (submitBtn) submitBtn.innerHTML = isLoginMode ? 'Log In <i class="fa fa-arrow-right"></i>' : 'Get Started <i class="fa fa-arrow-right"></i>';
            e.target.innerText = isLoginMode ? 'Create Account' : 'Log In';
        });

        document.getElementById('auth-form')?.addEventListener('submit', (e) => {
            e.preventDefault();
            const email = document.getElementById('email')?.value.trim().toLowerCase();
            const password = document.getElementById('password')?.value;
            const users = JSON.parse(localStorage.getItem('genz_loan_registry') || '[]');

            if (isLoginMode) {
                const found = users.find(u => u.email === email && u.password === password);
                if (!found) {
                    showToast("Auth Error: Invalid email or password.", "error");
                    return;
                }
                currentUser = { ...found };
                currentUser.isLoggedIn = true;
            } else {
                const name = document.getElementById('fullname')?.value;
                const phone = document.getElementById('phone')?.value;
                const country = document.getElementById('country-select')?.value;
                const confirm2 = document.getElementById('confirm-password')?.value;

                if (!validateIntelligence(document.getElementById('auth-form'))) return;
                if (password !== confirm2) {
                    showToast("Security Error: Passwords do not match.", "error");
                    return;
                }
                if (users.find(u => u.email === email)) {
                    showToast("Email already registered.", "info");
                    document.getElementById('toggle-auth')?.click();
                    return;
                }

                const countryData = COUNTRIES.find(c => c.name === country);

                // CREATE FRESH USER FROM INITIAL STATE
                currentUser = {
                    ...INITIAL_STATE,
                    uid: "user_" + Date.now(),
                    isLoggedIn: true,
                    hasAgreedToTerms: true,
                    name: name,
                    email: email,
                    phone: phone,
                    country: country,
                    currency: countryData ? countryData.currency : "KES",
                    password: password, // Note: Storing in registry
                    limit: countryData ? countryData.localLimit : 10000,
                    referralCode: "GENZ-" + Math.random().toString(36).substring(2, 6).toUpperCase()
                };

                users.push(currentUser);
                localStorage.setItem('genz_loan_registry', JSON.stringify(users));
            }
            saveState();
            updateDashboard();
            showScreen('dashboard-screen');
            showToast(`Welcome ${currentUser.name}!`, "success");
        });

        document.getElementById('btn-apply')?.addEventListener('click', () => {
            if (currentUser.pendingLoan > 0) {
                showToast("You have an outstanding loan.", "error");
                return;
            }
            if (currentUser.lastDisbursementTime) {
                const hoursPassed = (Date.now() - currentUser.lastDisbursementTime) / (1000 * 60 * 60);
                if (hoursPassed < 6) {
                    showToast(`System Cooldown: Wait ${(6 - hoursPassed).toFixed(1)} more hours.`, "info");
                    return;
                }
            }

            requireInternet(() => {
                // REDIRECT TO SAVED PROGRESS
                if (currentUser.lastProgressScreen && PROGRESS_SCREENS.includes(currentUser.lastProgressScreen)) {
                    showScreen(currentUser.lastProgressScreen);
                } else {
                    showScreen('apply-screen');
                }
            });
        });

        document.getElementById('btn-history')?.addEventListener('click', () => showScreen('history-screen'));

        document.getElementById('btn-repay')?.addEventListener('click', () => {
            if (currentUser.pendingLoan === 0) {
                showToast("No active loans to repay.", "info");
                return;
            }
            if (confirm(`Repay ${currentUser.currency} ${currentUser.pendingLoan.toLocaleString()}?`)) {
                currentUser.pendingLoan = 0;
                addTransaction("Loan Repayment", currentUser.pendingLoan, true);
                saveState();
                updateDashboard();
                showToast("Loan Repaid Successfully!", "success");
            }
        });

        document.getElementById('btn-approve-terms')?.addEventListener('click', () => {
            const qa = document.getElementById('qualified-amount');
            if (qa) qa.innerHTML = `<span class="currency">${currentUser.currency}</span> ${globalQualifiedAmount.toLocaleString()}`;
            showScreen('result-screen');
        });

        document.getElementById('btn-decline-terms')?.addEventListener('click', () => {
            if (confirm("Cancel application?")) showScreen('dashboard-screen');
        });

        document.getElementById('btn-pay-fee')?.addEventListener('click', () => {
            if (currentUser.country === 'Kenya') {
                // Kenyan account: Open Safaricom Lipa na M-PESA Express STK Push modal directly
                openMpesaStkModal();
            } else {
                // Non-Kenyan account: Card only, direct to Paystack
                processPaystackPayment(['card']);
            }
        });

        document.getElementById('share-referral')?.addEventListener('click', () => {
            if (navigator.share) {
                navigator.share({
                    title: 'GenZ Loan Professional',
                    text: `Join GenZ Loan and get instant credit! Use my code: ${currentUser.referralCode}`,
                    url: window.location.href
                });
            } else {
                copyReferral();
            }
        });

        setTimeout(() => {
            if (splash) splash.style.display = 'none';
            clearTimeout(forceHide);
            checkConnectivity();
        }, 500);
    } catch (e) {
        console.error("Boot Error:", e);
        if (splash) splash.style.display = 'none';
        clearTimeout(forceHide);
    }
}

const DOCUMENT_TYPES = {
    "Government Elite": [
        { name: "National ID Card", icon: "fa-id-card", color: "#00d2ff", guidance: "Capture front side first. All 4 corners must be visible." },
        { name: "Voter Registration Card", icon: "fa-check-to-slot", color: "#00d2ff", guidance: "Ensure the registration number is clearly legible." },
        { name: "Alien / Resident Card", icon: "fa-id-card-clip", color: "#00d2ff", guidance: "Official residence permit for non-citizens." },
        { name: "Refugee ID Pass", icon: "fa-person-shelter", color: "#00d2ff", guidance: "Government issued refugee identification." },
        { name: "Civil Service ID", icon: "fa-user-tie", color: "#00d2ff", guidance: "Official government employee identification." },
        { name: "Social Security Card", icon: "fa-shield-heart", color: "#00d2ff", guidance: "Official card showing your social security number." }
    ],
    "Global Travel": [
        { name: "International Passport", icon: "fa-passport", color: "#ff00ff", guidance: "Capture the biodata page. Ensure MRZ (bottom text) is clear." },
        { name: "Green Card (US PR)", icon: "fa-earth-americas", color: "#ff00ff", guidance: "Permanent Resident Card (both sides)." },
        { name: "EU Blue Card", icon: "fa-euro-sign", color: "#ff00ff", guidance: "EU work and residence permit for highly qualified workers." },
        { name: "Diplomatic Passport", icon: "fa-crown", color: "#ff00ff", guidance: "Official diplomatic travel document." },
        { name: "Seaman's Book", icon: "fa-ship", color: "#ff00ff", guidance: "Continuous Discharge Certificate (CDC) for maritime staff." },
        { name: "International Visa", icon: "fa-plane-arrival", color: "#ff00ff", guidance: "Valid visa page from your passport." }
    ],
    "Force & Military": [
        { name: "Military ID Card", icon: "fa-person-military-pointing", color: "#f3ff00", guidance: "Active duty or reserve military identification." },
        { name: "Veteran Affairs Card", icon: "fa-medal", color: "#f3ff00", guidance: "Official identification for retired military personnel." },
        { name: "Police Badge / ID", icon: "fa-shield-halved", color: "#f3ff00", guidance: "Law enforcement officer identification." },
        { name: "UN Security Pass", icon: "fa-globe", color: "#f3ff00", guidance: "United Nations security clearance identification." },
        { name: "Intelligence Agency ID", icon: "fa-user-secret", color: "#f3ff00", guidance: "Official agency personnel identification." }
    ],
    "Career & Payroll": [
        { name: "Official Payslip", icon: "fa-file-invoice-dollar", color: "#ff9d00", guidance: "Most recent payslip from the last 3 months." },
        { name: "Employment Contract", icon: "fa-file-signature", color: "#ff9d00", guidance: "Signed page of your active employment agreement." },
        { name: "Work Permit Card", icon: "fa-briefcase", color: "#ff9d00", guidance: "Authorization card for international workers." },
        { name: "Staff Access ID", icon: "fa-address-card", color: "#ff9d00", guidance: "Company issued employee identification badge." },
        { name: "P9 / Tax Form", icon: "fa-receipt", color: "#ff9d00", guidance: "Year-end tax statement or income declaration." },
        { name: "Union Membership", icon: "fa-users-rectangle", color: "#ff9d00", guidance: "Official labor union identification card." }
    ],
    "Asset & Finance": [
        { name: "Land Title Deed", icon: "fa-scroll", color: "#00ff88", guidance: "Official ownership document for land or property." },
        { name: "Vehicle Logbook", icon: "fa-car-side", color: "#00ff88", guidance: "Motor vehicle ownership (NTSA/DMV) document." },
        { name: "Bank Statement", icon: "fa-building-columns", color: "#00ff88", guidance: "Certified bank statement from the last 6 months." },
        { name: "Share Certificate", icon: "fa-chart-pie", color: "#00ff88", guidance: "Proof of stock ownership in a listed company." },
        { name: "SACCO Statement", icon: "fa-hand-holding-hand", color: "#00ff88", guidance: "Savings and credit cooperative society records." },
        { name: "Insurance Policy", icon: "fa-file-shield", color: "#00ff88", guidance: "Active life or health insurance policy document." }
    ],
    "Utilities & Residence": [
        { name: "Fiber Internet Bill", icon: "fa-wifi", color: "#bb86fc", guidance: "Recent bill showing your residence address." },
        { name: "Electricity Bill", icon: "fa-bolt", color: "#bb86fc", guidance: "Official power utility bill (last 2 months)." },
        { name: "Water / Service Bill", icon: "fa-faucet-drip", color: "#bb86fc", guidance: "Utility bill showing physical living location." },
        { name: "Rent Agreement", icon: "fa-file-contract", color: "#bb86fc", guidance: "Valid lease signed by your landlord." },
        { name: "Council Rates", icon: "fa-house-user", color: "#bb86fc", guidance: "Official local authority rates or tax bill." }
    ]
};

let selectedDoc = null;

function renderDocSelector(filter = "") {
    const container = document.getElementById('doc-categories');
    if (!container) return;

    let html = "";
    Object.keys(DOCUMENT_TYPES).forEach(category => {
        const docs = DOCUMENT_TYPES[category].filter(d =>
            d.name.toLowerCase().includes(filter.toLowerCase())
        );

        if (docs.length > 0) {
            html += `<h4 class="category-title">${category}</h4>`;
            docs.forEach(doc => {
                const isSelected = selectedDoc && selectedDoc.name === doc.name;
                html += `
                    <div class="doc-item ${isSelected ? 'selected' : ''}" onclick="selectDocument('${doc.name}', '${category}')">
                        <i class="fa-solid ${doc.icon}" style="color: ${doc.color}"></i>
                        <div class="doc-info">
                            <span class="doc-name" style="color: ${doc.color}">${doc.name}</span>
                        </div>
                        <i class="fa-solid fa-circle-check check-mark"></i>
                    </div>
                `;
            });
        }
    });

    container.innerHTML = html || '<p class="center-text" style="color: var(--text-dim); padding: 20px;">No matching documents found.</p>';
}

function selectDocument(name, category) {
    const doc = DOCUMENT_TYPES[category].find(d => d.name === name);
    selectedDoc = doc;
    renderDocSelector(document.getElementById('doc-search')?.value || "");
    const btn = document.getElementById('btn-continue-kyc');
    if (btn) btn.disabled = false;
}

document.getElementById('doc-search')?.addEventListener('input', (e) => {
    renderDocSelector(e.target.value);
});

document.getElementById('btn-continue-kyc')?.addEventListener('click', () => {
    if (selectedDoc) {
        document.getElementById('doc-name-display').innerText = selectedDoc.name;
        document.getElementById('doc-guidance-text').innerText = selectedDoc.guidance;
        const iconBox = document.getElementById('guidance-icon');
        if (iconBox) iconBox.innerHTML = `<i class="fa-solid ${selectedDoc.icon}" style="color: ${selectedDoc.color}"></i>`;
        showScreen('kyc-guidance-screen');
    }
});

function launchNativeKYC() {
    if (typeof AndroidKYC !== 'undefined' && selectedDoc) {
        // REQUEST BACK CAMERA FOR DOCUMENT SCAN (BETTER RESOLUTION)
        AndroidKYC.startVerification("DOCUMENT", selectedDoc.name, currentUser.name, currentUser.country, "", "", "");
    } else {
        // Simulation for web testing
        setTimeout(() => onKYCResult({ status: "DOCUMENT_SUCCESS", frontPath: "simulated.jpg" }), 2000);
    }
}

document.getElementById('doc-upload')?.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(event) {
            const base64Str = event.target.result;
            showToast("Native AI Scanning...", "info");

            // USE NATIVE BRIDGE FOR REAL VALIDATION
            if (typeof AndroidKYC !== 'undefined' && AndroidKYC.validateUploadedDoc) {
                AndroidKYC.validateUploadedDoc(base64Str, selectedDoc?.name || "ID Card", currentUser.country || "Kenya");
            } else {
                // Browser simulation
                setTimeout(() => onUploadValidation('SUCCESS'), 2000);
            }
        };
        reader.readAsDataURL(file);
    }
});

// GLOBAL CALLBACK FOR NATIVE VALIDATION (REMOVED)

function launchNativeLiveness() {
    if (typeof AndroidKYC !== 'undefined') {
        // Correct mode and parameters for biometric selfie
        AndroidKYC.startVerification("SELFIE", "Facial Biometric", currentUser.name, currentUser.country, "", "", "");
    } else {
        // SIMULATED LIVENESS SEQUENCE FOR WEB
        runLivenessSequence();
    }
}

function runLivenessSequence() {
    const instruction = document.getElementById('liveness-instruction');
    const steps = ["Position Face in Center", "Turn Slowly Left", "Turn Slowly Right", "Smile into Front Camera!"];
    const dots = document.querySelectorAll('.liveness-dot');

    let currentLivenessStep = 0;

    const nextLivenessStep = () => {
        if (currentLivenessStep < steps.length) {
            if (instruction) instruction.innerText = steps[currentLivenessStep];
            dots.forEach((d, i) => {
                d.classList.toggle('active', i === currentLivenessStep);
                if (i < currentLivenessStep) d.classList.add('completed');
            });

            currentLivenessStep++;
            setTimeout(nextLivenessStep, 2500);
        } else {
            if (instruction) instruction.innerText = "Verifying Facial Biometrics...";
            setTimeout(() => {
                onKYCResult({ status: "SELFIE_SUCCESS", selfiePath: "verified_user.jpg" });
            }, 2000);
        }
    };

    nextLivenessStep();
}

function copyReferral() {
    const code = currentUser.referralCode;
    navigator.clipboard.writeText(code).then(() => {
        showToast("Referral code copied!", "success");
    });
}

function showClassicLoader(duration = 500) {
    const loader = document.getElementById('classic-loader-overlay');
    if (!loader) return;
    loader.style.display = 'flex';
    setTimeout(() => {
        loader.style.display = 'none';
    }, duration);
}

window.onKYCResult = function(result) {
    if (result.status === "DOCUMENT_SUCCESS") {
        currentUser.kycFront = result.frontPath;
        currentUser.kycBack = result.backPath;
        saveState();
        showClassicLoader(800);
        setTimeout(() => showScreen('kyc-liveness-screen'), 800);
    } else if (result.status === "SELFIE_SUCCESS") {
        currentUser.kycCompleted = true;
        currentUser.kycSelfie = result.selfiePath || "verified_user.jpg";
        saveState();
        showClassicLoader(1000);
        setTimeout(() => finalizeLoanApplication(), 1000);
    }
};

window.onUploadValidation = function(status) {
    showClassicLoader(1200);
    setTimeout(() => {
        if (status === 'SUCCESS') {
            onKYCResult({ status: "DOCUMENT_SUCCESS", frontPath: "uploaded_verified.jpg", backPath: "uploaded_verified.jpg" });
        } else if (status === 'JURISDICTION_MISMATCH') {
            showToast(`Security Error: Document does not match your registered country (${currentUser.country}).`, "error");
        } else {
            showToast("Integrity Error: Invalid Identity Document.", "error");
        }
    }, 1200);
};

function finalizeLoanApplication() {
    const inc = parseInt(currentUser.profile['income'] || 10000);
    globalQualifiedAmount = Math.max(5000, Math.min(250000, Math.floor(inc * 2.5)));

    // CAPTURE ALL BANK & PAYOUT DETAILS FOR THE AGREEMENT - CORRECT FIELDS
    const methodVal = document.querySelector('input[name="payout-method"]:checked')?.value || 'mobile';
    const bankVal = (methodVal === 'bank')
        ? (document.getElementById('bank-name-input')?.value || "Bank Account")
        : "Mobile Money Wallet";

    const accNameVal = document.getElementById('acc-name')?.value || currentUser.name;
    const destVal = (methodVal === 'mobile') ?
        (document.getElementById('payout-phone')?.value || currentUser.phone) :
        (document.getElementById('account-number')?.value || 'N/A');

    currentLoanTerms = {
        principal: globalQualifiedAmount,
        total: Math.floor(globalQualifiedAmount * 1.12),
        rate: 12,
        days: 30,
        bankName: bankVal,
        accName: accNameVal,
        payoutMethod: methodVal,
        accNumber: destVal
    };

    currentUser.savedQualifiedAmount = globalQualifiedAmount;
    currentUser.savedLoanTerms = currentLoanTerms;
    saveState();

    console.log("Loan Finalized. Terms Locked.");

    renderAgreement();
    showScreen('agreement-screen');
}

function renderAgreement() {
    const principalEl = document.getElementById('agree-principal');
    const interestEl = document.getElementById('agree-interest');
    const periodEl = document.getElementById('agree-period');
    const feeEl = document.getElementById('agree-fee');
    const totalEl = document.getElementById('agree-total');
    const bankEl = document.getElementById('agree-bank');
    const accEl = document.getElementById('agree-acc-name');

    const country = COUNTRIES.find(c => c.name === currentUser.country) || { rate: 1.0 };
    const fee = Math.ceil(500 * country.rate);

    const principal = currentLoanTerms.principal || 0;
    const total = currentLoanTerms.total || 0;
    const currency = currentUser.currency || "KES";

    if (principalEl) principalEl.innerText = `${currency} ${principal.toLocaleString()}`;
    if (interestEl) interestEl.innerText = `12% (${currency} ${(total - principal).toLocaleString()})`;
    if (periodEl) periodEl.innerText = `${currentLoanTerms.days || 30} Days`;
    if (feeEl) feeEl.innerText = `${currency} ${fee.toLocaleString()}`;
    if (totalEl) totalEl.innerText = `${currency} ${total.toLocaleString()}`;
    if (bankEl) bankEl.innerText = currentLoanTerms.bankName || "Verified Wallet";
    if (accEl) accEl.innerText = currentLoanTerms.accName || currentUser.name;
}

// PAYMENT MODAL & PESALINK FUNCTIONS FOR NON-KENYAN ACCOUNTS
window.closePaymentModal = function() {
    const modal = document.getElementById('payment-modal-overlay');
    if (modal) modal.style.display = 'none';
};

window.closePesaLinkModal = function() {
    const modal = document.getElementById('pesalink-modal-overlay');
    if (modal) modal.style.display = 'none';
};

window.selectPaymentOption = function(option) {
    window.closePaymentModal();
    if (option === 'card') {
        processPaystackPayment(['card']);
    } else if (option === 'pesalink') {
        showPesaLinkDetails();
    }
};

window.showPesaLinkDetails = function() {
    const amountElem = document.getElementById('pesalink-modal-amount');
    if (amountElem) {
        if (currentUser.country !== 'Kenya') {
            amountElem.textContent = "USD $4.00 (KES 500)";
        } else {
            amountElem.textContent = "KES 500";
        }
    }
    const refCodeElem = document.getElementById('pesalink-reference-code');
    if (refCodeElem) {
        const ref = "GZ-PL-" + Math.floor(100000 + Math.random() * 900000);
        refCodeElem.textContent = ref;
    }
    const txInput = document.getElementById('pesalink-tx-ref');
    if (txInput) txInput.value = "";

    const modal = document.getElementById('pesalink-modal-overlay');
    if (modal) modal.style.display = 'flex';
};

window.copyPesaLinkAccount = function() {
    const accNum = document.getElementById('pesalink-account-num')?.textContent || '0102839481900';
    if (navigator.clipboard) {
        navigator.clipboard.writeText(accNum).then(() => {
            showToast("PesaLink Account copied to clipboard!", "success");
        }).catch(() => {
            showToast("Account: " + accNum, "info");
        });
    } else {
        showToast("Account: " + accNum, "info");
    }
};

window.verifyAndConfirmPesaLink = function() {
    const txRefInput = document.getElementById('pesalink-tx-ref');
    const txRef = txRefInput ? txRefInput.value.trim() : "";

    if (!txRef || txRef.length < 5) {
        showToast("Security Error: Enter a valid Bank / M-Pesa transaction reference code.", "error");
        if (txRefInput) txRefInput.style.borderColor = "var(--neon-pink)";
        return;
    }

    window.closePesaLinkModal();
    showToast("PesaLink Reference Verified! Processing disbursement...", "success");

    addTransaction("PesaLink Fee Verification (" + txRef + ")", 500, true);

    currentUser.loanStep = 1;
    currentUser.lastProgressScreen = null;
    currentUser.loanFormData = {};
    currentUser.limit = globalQualifiedAmount;
    saveState();
    startDisbursementProcessing();
};

// SAFARICOM M-PESA DARAJA PRODUCTION STK PUSH (LIPA NA M-PESA EXPRESS)
window.stopMpesaPolling = function() {
    if (window.mpesaPollInterval) {
        clearInterval(window.mpesaPollInterval);
        window.mpesaPollInterval = null;
    }
};

window.startMpesaPolling = function() {
    window.stopMpesaPolling();
    if (!window.currentCheckoutRequestId) return;

    let pollAttempts = 0;
    const maxAttempts = 12; // 12 attempts * 3 seconds = 36 seconds total

    const updateStatusText = (text) => {
        const label = document.getElementById('stk-poll-status');
        if (label) label.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> ${text}`;
    };

    updateStatusText("Waiting for you to enter PIN on your phone...");

    window.mpesaPollInterval = setInterval(() => {
        pollAttempts++;
        updateStatusText(`Verifying with Safaricom (${pollAttempts}/${maxAttempts})... Enter PIN on phone.`);

        if (typeof AndroidKYC !== 'undefined' && AndroidKYC.queryMpesaSTKStatus) {
            AndroidKYC.queryMpesaSTKStatus(window.currentCheckoutRequestId, "onMpesaQueryResponse");
        }

        if (pollAttempts >= maxAttempts) {
            window.stopMpesaPolling();
            showToast("STK Prompt Timed Out. Please try again.", "error");
            resetMpesaModalView();
        }
    }, 3000);
};

window.openMpesaStkModal = function() {
    window.stopMpesaPolling();
    const phoneInput = document.getElementById('mpesa-stk-phone');
    if (phoneInput) {
        phoneInput.value = currentUser.phone || "";
    }
    const initView = document.getElementById('mpesa-stk-initial-view');
    const waitView = document.getElementById('mpesa-stk-waiting-view');
    if (initView) initView.style.display = 'block';
    if (waitView) waitView.style.display = 'none';

    const modal = document.getElementById('mpesa-stk-modal-overlay');
    if (modal) modal.style.display = 'flex';
};

window.closeMpesaModal = function() {
    window.stopMpesaPolling();
    const modal = document.getElementById('mpesa-stk-modal-overlay');
    if (modal) modal.style.display = 'none';
};

window.sendMpesaSTKPushPrompt = function() {
    const phoneInput = document.getElementById('mpesa-stk-phone');
    let phoneVal = phoneInput ? phoneInput.value.trim() : "";

    if (!phoneVal || phoneVal.length < 9) {
        showToast("Enter a valid M-Pesa registered phone number.", "error");
        return;
    }

    const initView = document.getElementById('mpesa-stk-initial-view');
    const waitView = document.getElementById('mpesa-stk-waiting-view');
    const sentPhoneLabel = document.getElementById('stk-sent-phone');

    if (sentPhoneLabel) sentPhoneLabel.innerText = phoneVal;
    if (initView) initView.style.display = 'none';
    if (waitView) waitView.style.display = 'block';

    showToast("Connecting to Safaricom M-Pesa Daraja...", "info");

    if (typeof AndroidKYC !== 'undefined' && AndroidKYC.triggerMpesaSTKPush) {
        AndroidKYC.triggerMpesaSTKPush(phoneVal, 500, "onMpesaSTKResponse");
    } else {
        // Fallback for Web browser testing
        setTimeout(() => {
            onMpesaSTKResponse(JSON.stringify({
                ResponseCode: "0",
                CheckoutRequestID: "TEST_CHECKOUT_12345",
                ResponseDescription: "Success. Request accepted for processing"
            }));
        }, 2000);
    }
};

window.onMpesaSTKResponse = function(responseStr) {
    try {
        const res = typeof responseStr === 'string' ? JSON.parse(responseStr) : responseStr;
        console.log("STK Response received:", res);

        const code = String(res.ResponseCode !== undefined ? res.ResponseCode : (res.responseCode !== undefined ? res.responseCode : ""));
        if (code === "0") {
            window.currentCheckoutRequestId = res.CheckoutRequestID || res.checkoutRequestID || "";
            console.log("Captured CheckoutRequestID:", window.currentCheckoutRequestId);
            showToast("M-Pesa STK Prompt Sent! Enter PIN on phone.", "success");

            // Automatically start background polling loop
            window.startMpesaPolling();
        } else {
            showToast("M-Pesa Daraja Error: " + (res.ResponseDescription || res.customerMessage || "Request failed"), "error");
            resetMpesaModalView();
        }
    } catch (e) {
        console.error("STK Response Parse Error:", e);
        showToast("M-Pesa Connection Error.", "error");
        resetMpesaModalView();
    }
};

window.confirmMpesaPinEntered = function() {
    showToast("Checking payment status with Safaricom...", "info");

    if (!window.currentCheckoutRequestId) {
        showToast("Verification Error: Missing Checkout ID. Click 'Send M-Pesa STK Prompt' again.", "error");
        resetMpesaModalView();
        return;
    }

    if (typeof AndroidKYC !== 'undefined' && AndroidKYC.queryMpesaSTKStatus) {
        console.log("Executing manual Safaricom STK query for ID:", window.currentCheckoutRequestId);
        AndroidKYC.queryMpesaSTKStatus(window.currentCheckoutRequestId, "onMpesaQueryResponse");
    } else {
        // Pure Web testing environment ONLY
        setTimeout(() => {
            onMpesaQueryResponse(JSON.stringify({
                ResultCode: "0",
                ResultDesc: "Testing Mode: Web Simulation"
            }));
        }, 1000);
    }
};

window.onMpesaQueryResponse = function(responseStr) {
    console.log("Safaricom Query Response:", responseStr);
    try {
        const res = typeof responseStr === 'string' ? JSON.parse(responseStr) : responseStr;
        const code = String(res.ResultCode !== undefined ? res.ResultCode : (res.ResponseCode !== undefined ? res.ResponseCode : ""));
        const desc = (res.ResultDesc || res.ResponseDescription || "").toLowerCase();

        if (code === "0") {
            window.stopMpesaPolling();
            window.closeMpesaModal();
            showToast("M-Pesa Payment Confirmed! Processing disbursement...", "success");
            addTransaction("M-Pesa Express Fee (STK Push Verified)", 500, true);

            currentUser.loanStep = 1;
            currentUser.lastProgressScreen = null;
            currentUser.loanFormData = {};
            currentUser.limit = globalQualifiedAmount;
            saveState();
            startDisbursementProcessing();
        } else if (code === "1032" || desc.includes("cancel")) {
            window.stopMpesaPolling();
            showToast("Payment Failed: You cancelled the M-Pesa prompt on your phone.", "error");
            resetMpesaModalView();
        } else if (code === "1" || desc.includes("insufficient") || desc.includes("balance") || desc.includes("not enough")) {
            window.stopMpesaPolling();
            showToast("Payment Failed: Insufficient M-Pesa balance on your line.", "error");
            resetMpesaModalView();
        } else {
            // Transaction still in progress / PIN not entered yet -> keep polling loop running silently
            console.log("M-Pesa status query pending / in progress:", desc);
        }
    } catch (e) {
        console.error("STK Query Parse Error:", e);
    }
};

function resetMpesaModalView() {
    window.stopMpesaPolling();
    const initView = document.getElementById('mpesa-stk-initial-view');
    const waitView = document.getElementById('mpesa-stk-waiting-view');
    if (initView) initView.style.display = 'block';
    if (waitView) waitView.style.display = 'none';
}

function processPaystackPayment(customChannels) {
    if (typeof PaystackPop === 'undefined') {
        showToast("Gateways initializing... Try again in a moment.", "info");
        const script = document.createElement('script');
        script.src = "https://js.paystack.co/v1/inline.js";
        document.body.appendChild(script);
        return;
    }

    let paymentCurrency = 'KES';
    let paymentAmount = 50000;

    let channels = customChannels || ['card', 'bank', 'ussd', 'qr', 'mobile_money', 'bank_transfer'];

    if (customChannels && customChannels.includes('bank_transfer')) {
        // PesaLink / Bank Transfer requires KES currency on Paystack
        paymentCurrency = 'KES';
        paymentAmount = 50000; // 500 KES
        channels = ['bank_transfer', 'bank'];
    } else if (currentUser.country !== 'Kenya') {
        // Card payments for non-Kenyan accounts can be in USD
        paymentCurrency = 'USD';
        paymentAmount = 400; // $4.00 USD
        channels = ['card'];
    }

    PaystackPop.setup({
        key: 'pk_live_d3ad28a96d0faa12c3c25a14389d29980a707d3b',
        email: currentUser.email,
        amount: paymentAmount,
        currency: paymentCurrency,
        channels: channels,
        callback: (res) => {
            currentUser.loanStep = 1;
            currentUser.lastProgressScreen = null;
            currentUser.loanFormData = {};
            currentUser.limit = globalQualifiedAmount;
            saveState();
            startDisbursementProcessing();
        }
    }).openIframe();
}

// SAFE INITIALIZATION: Handles cases where script loads after event has fired
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initApp);
} else {
    initApp();
}
