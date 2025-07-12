//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ru.nedan.util.animation.util;

public final class Easings {
    public static final double c1 = 1.70158;
    public static final double c2 = 2.5949095;
    public static final double c3 = 2.70158;
    public static final double c4 = 2.0943951023931953;
    public static final double c5 = 1.3962634015954636;
    public static final Easing NONE = (value) -> {
        return value;
    };
    public static final Easing QUAD_IN = powIn(2);
    public static final Easing QUAD_OUT = powOut(2);
    public static final Easing QUAD_BOTH = powBoth(2.0);
    public static final Easing CUBIC_IN = powIn(3);
    public static final Easing CUBIC_OUT = powOut(3);
    public static final Easing CUBIC_BOTH = powBoth(3.0);
    public static final Easing QUART_IN = powIn(4);
    public static final Easing QUART_OUT = powOut(4);
    public static final Easing QUART_BOTH = powBoth(4.0);
    public static final Easing QUINT_IN = powIn(5);
    public static final Easing QUINT_OUT = powOut(5);
    public static final Easing QUINT_BOTH = powBoth(5.0);
    public static final Easing SINE_IN = (value) -> {
        return 1.0 - Math.cos(value * Math.PI / 2.0);
    };
    public static final Easing SINE_OUT = (value) -> {
        return Math.sin(value * Math.PI / 2.0);
    };
    public static final Easing SINE_BOTH = (value) -> {
        return -(Math.cos(Math.PI * value) - 1.0) / 2.0;
    };
    public static final Easing CIRC_IN = (value) -> {
        return 1.0 - Math.sqrt(1.0 - Math.pow(value, 2.0));
    };
    public static final Easing CIRC_OUT = (value) -> {
        return Math.sqrt(1.0 - Math.pow(value - 1.0, 2.0));
    };
    public static final Easing CIRC_BOTH = (value) -> {
        return value < 0.5 ? (1.0 - Math.sqrt(1.0 - Math.pow(2.0 * value, 2.0))) / 2.0 : (Math.sqrt(1.0 - Math.pow(-2.0 * value + 2.0, 2.0)) + 1.0) / 2.0;
    };
    public static final Easing ELASTIC_IN = (value) -> {
        return value != 0.0 && value != 1.0 ? Math.pow(-2.0, 10.0 * value - 10.0) * Math.sin((value * 10.0 - 10.75) * 2.0943951023931953) : value;
    };
    public static final Easing ELASTIC_OUT = (value) -> {
        return value != 0.0 && value != 1.0 ? Math.pow(2.0, -10.0 * value) * Math.sin((value * 10.0 - 0.75) * 2.0943951023931953) + 1.0 : value;
    };
    public static final Easing ELASTIC_BOTH = (value) -> {
        if (value != 0.0 && value != 1.0) {
            return value < 0.5 ? -(Math.pow(2.0, 20.0 * value - 10.0) * Math.sin((20.0 * value - 11.125) * 1.3962634015954636)) / 2.0 : Math.pow(2.0, -20.0 * value + 10.0) * Math.sin((20.0 * value - 11.125) * 1.3962634015954636) / 2.0 + 1.0;
        } else {
            return value;
        }
    };
    public static final Easing EXPO_IN = (value) -> {
        return value != 0.0 ? Math.pow(2.0, 10.0 * value - 10.0) : value;
    };
    public static final Easing EXPO_OUT = (value) -> {
        return value != 1.0 ? 1.0 - Math.pow(2.0, -10.0 * value) : value;
    };
    public static final Easing EXPO_BOTH = (value) -> {
        if (value != 0.0 && value != 1.0) {
            return value < 0.5 ? Math.pow(2.0, 20.0 * value - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * value + 10.0)) / 2.0;
        } else {
            return value;
        }
    };
    public static final Easing BACK_IN = (value) -> {
        return 2.70158 * Math.pow(value, 3.0) - 1.70158 * Math.pow(value, 2.0);
    };
    public static final Easing BACK_OUT = (value) -> {
        return 1.0 + 2.70158 * Math.pow(value - 1.0, 3.0) + 1.70158 * Math.pow(value - 1.0, 2.0);
    };
    public static final Easing BACK_BOTH = (value) -> {
        return value < 0.5 ? Math.pow(2.0 * value, 2.0) * (7.189819 * value - 2.5949095) / 2.0 : (Math.pow(2.0 * value - 2.0, 2.0) * (3.5949095 * (value * 2.0 - 2.0) + 2.5949095) + 2.0) / 2.0;
    };
    public static final Easing BOUNCE_OUT = (x) -> {
        double n1 = 7.5625;
        double d1 = 2.75;
        if (x < 1.0 / d1) {
            return n1 * Math.pow(x, 2.0);
        } else if (x < 2.0 / d1) {
            return n1 * Math.pow(x - 1.5 / d1, 2.0) + 0.75;
        } else {
            return x < 2.5 / d1 ? n1 * Math.pow(x - 2.25 / d1, 2.0) + 0.9375 : n1 * Math.pow(x - 2.625 / d1, 2.0) + 0.984375;
        }
    };
    public static final Easing BOUNCE_IN = (value) -> {
        return 1.0 - BOUNCE_OUT.ease(1.0 - value);
    };
    public static final Easing BOUNCE_BOTH = (value) -> {
        return value < 0.5 ? (1.0 - BOUNCE_OUT.ease(1.0 - 2.0 * value)) / 2.0 : (1.0 + BOUNCE_OUT.ease(2.0 * value - 1.0)) / 2.0;
    };

    private Easings() {
    }

    public static Easing powIn(double n) {
        return (value) -> {
            return Math.pow(value, n);
        };
    }

    public static Easing powIn(int n) {
        return powIn((double)n);
    }

    public static Easing powOut(double n) {
        return (value) -> {
            return 1.0 - Math.pow(1.0 - value, n);
        };
    }

    public static Easing powOut(int n) {
        return powOut((double)n);
    }

    public static Easing powBoth(double n) {
        return (value) -> {
            return value < 0.5 ? Math.pow(2.0, n - 1.0) * Math.pow(value, n) : 1.0 - Math.pow(-2.0 * value + 2.0, n) / 2.0;
        };
    }
}
