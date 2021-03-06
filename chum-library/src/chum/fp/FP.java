package chum.fp;

/**
 * 16:16 fixed point math routines.
 * <p>
 * A fixed point number is a 32 bit int containing 16 bits of integer and 16 bits of fraction.
 * Max range for FP is [-32767,32767]
 * <p>
 * Many of the routines were adapted from
 * http://www.beartronics.com/imode/fplib/index.html
 */
public class FP {

    /** The FP value for 0 */
    public static final int ZERO = 0x00000;

    /** The FP value for 1 */
    public static final int ONE = 0x10000;

    /** The FP value for 2 */
    public static final int TWO = 0x20000;

    /** The FP value for -1 */
    public static final int NEG_ONE = 0xffff0000;

    /** The FP value for 0.5 */
    public static final int HALF = ONE >> 1;

    /** The maximum positive value possible for a FP (~32768) */
    public static final int MAX = 0x7fffffff;

    /** The minimum negative value possible for a FP (~-32768) */
    public static final int MIN = 0x80000000; // not sure about this


    /** Convert a 16:16 fixed-point to an int
        @param x a FP value
        @return the FP value converted to a normal integer
     */
    public static int toInt (int x) {
	return x>>16;
    }

    /** Convert a 16:16 fixed-point to a float
        @param x FP value
        @return the FP value converted to a float
     */
    public static float toFloat (int x) {
        return ((float)x)/65536.0f;
    }

    /** Convert a float to a 16:16 fixed-point
        @param x floating-point value
        @return the corresponding FP value
     */
    public static int floatToFP (float x) {
        return (int)(x * 65536F);
    }

    /** Convert an int to a 16:16 fixed-point 
        @param x integer value
        @return the corresponding FP value
     */
    public static int intToFP (int x) {
	return x<<16;
    }

    /** Convert a long to a 16:16 fixed-point 
        @param x long value
        @return the corresponding FP value
     */
    public static int longToFP (long x) {
	return (int)(x<<16);
    }

    /** Multiply two fixed-point numbers
        @param x FP value
        @param y FP value
        @return the product of the two numbers, as a FP value
     */
    public static int mul (int x, int y) {
	long z = (long) x * (long) y;
	return ((int) (z >> 16));
    }

    /** Divides two fixed-point numbers
        @param x FP value
        @param y FP value
        @return the quotient of the two numbers, as a FP value
     */
    public static int div (int x, int y) {
	long z = (((long) x) << 32);
	return (int) ((z / y) >> 16);
    }


    /** Compute square-root of a 16:16 fixed point number
        @param n FP value
        @return the square root as a FP valae
     */
    public static int sqrt (int n) {
        if ( n == 0 ) return 0;
        if ( n == FP.ONE ) return FP.ONE;

	int s = (n + 65536) >> 1;
	for (int i = 0; i < 8; i++) {
	    //converge six times
	    s = (s + div(n, s)) >> 1;
	}
	return s;
    }

    /** Round to nearest fixed point integer
        @param n FP value
        @return the value rounded to the nearest whole number, as a FP value
     */
    public static int round (int n) {
	if (n > 0) {
	    if ((n & 0x8000) != 0) {
		return (((n+0x10000)>>16)<<16);
	    } else {
		return (((n)>>16)<<16);
	    }
	} else {
	    int k;
	    n = -n;
	    if ((n & 0x8000) != 0) {
		k = (((n+0x10000)>>16)<<16);
	    } else {
		k = (((n)>>16)<<16);
	    }
	    return -k;
	}
    }

    /** The FP value representing PI */
    public static final int PI = 205887;

    /** The FP value representing PI/2 */
    public static final int PI_OVER_2 = PI >> 1;

    /** The FP value representing PI*2 */
    public static final int PI_TIMES_2 = PI << 1;

    /** The FP value representing PI/180 (conversion from degrees to radians) */
    public static final int PI_OVER_180 = 1144;

    /** The FP value representing 180/PI (convertion from radians to degrees) */
    public static final int _180_OVER_PI = 3754936;

    /** The FP value for e */
    public static final int E = 178145;


    /** Convert degrees to radians 
        @param deg FP value representing degrees
        @return the corresponding FP value representing radians
     */
    public static final int deg2rad(int deg) {
        return mul(deg,PI_OVER_180);
    }
       
    /** Convert radians to degrees
        @param rad FP value representing radians
        @return the corresponding FP value representing degrees
    */
    public static final int rad2deg(int rad) {
        return mul(rad,_180_OVER_PI);
    }
       
    /*
      For the inverse tangent calls, all approximations are valid for
      |t| <= 1.  To compute ATAN(t) for t > 1, use ATAN(t) = PI/2 -
      ATAN(1/t).  For t < -1, use ATAN(t) = -PI/2 - ATAN(1/t).
    */

    static final int SK1 = 498;
    static final int SK2 = 10882;

    /** Computes sin(f)
        @param f is a FP value in radians (0 <= f <= 2PI)
        @return the sin of the number (-1,1) as a FP value
    */
    public static int sin (int f) {
        // todo: fix accuracy:
        return FP.floatToFP((float)Math.sin((double)FP.toFloat(f)));

// 	// If in range -pi/4 to pi/4: nothing needs to be done.
// 	// otherwise, we need to get f into that range and account for
// 	// sign change.

// 	int sign = 1;
// 	if ((f > PI_OVER_2) && (f <= PI)) {
// 	    f = PI - f;
// 	} else if ((f > PI) && (f <= (PI + PI_OVER_2))) {
// 	    f = f - PI;
// 	    sign = -1;
// 	} else if (f > (PI + PI_OVER_2)) {
// 	    f = (PI<<1)-f;
// 	    sign = -1;
// 	}

// 	int sqr = mul(f,f);
// 	int result = SK1;
// 	result = mul(result, sqr);
// 	result -= SK2;
// 	result = mul(result, sqr);
// 	result += (1<<16);
// 	result = mul(result, f);
// 	return sign * result;
    }

    static final int CK1 = 2328;
    static final int CK2 = 32551;

    /** Computes cos(f)
        @param f is a FP value in radians (0 <= f <= PI/2)
        @return the cos of the number (-1,1) as a FP value
     */
    public static int cos (int f) {
        // todo: fix accuracy:
        return FP.floatToFP((float)Math.cos((double)FP.toFloat(f)));

// 	int sign = 1;
// 	if ((f > PI_OVER_2) && (f <= PI)) {
// 	    f = PI - f;
// 	    sign = -1;
// 	} else if ((f > PI_OVER_2) && (f <= (PI + PI_OVER_2))) {
// 	    f = f - PI;
// 	    sign = -1;
// 	} else if (f > (PI + PI_OVER_2)) {
// 	    f = (PI<<1)-f;
// 	}

// 	int sqr = mul(f,f);
// 	int result = CK1;
// 	result = mul(result, sqr);
// 	result -= CK2;
// 	result = mul(result, sqr);
// 	result += (1<<16);
// 	return result * sign;
    }


    static final int TK1 = 13323;
    static final int TK2 = 20810;

    /** Computes tan(f)
        @param f is a FP value in radians (0 <= f <= PI/4)
        @return the tan of the number as a FP value
     */

    public static int tan (int f) {
        // todo: fix accuracy:
        return FP.floatToFP((float)Math.tan((double)FP.toFloat(f)));

// 	int sqr = mul(f,f);
// 	int result = TK1;
// 	result = mul(result, sqr);
// 	result += TK2;
// 	result = mul(result, sqr);
// 	result += (1<<16);
// 	result = mul(result, f);
// 	return result;
    }


    /** Computes atan(f)
        @param f is a FP number |f| <= 1
        @return the atan as a FP value
        <p>
        For the inverse tangent calls, all approximations are valid
        for |t| <= 1.
        To compute ATAN(t) for t > 1, use ATAN(t) = PI/2 - ATAN(1/t).
        For t < -1, use ATAN(t) = -PI/2 - ATAN(1/t).
     */

    public static int atan (int f) {
        // todo: fix accuracy:
        return FP.floatToFP((float)Math.atan((double)FP.toFloat(f)));

// 	int sqr = mul(f,f);
// 	int result = 1365;
// 	result = mul(result, sqr);
// 	result -= 5579;
// 	result = mul(result, sqr);
// 	result += 11805;
// 	result = mul(result, sqr);
// 	result -= 21646;
// 	result = mul(result, sqr);
// 	result += 65527;
// 	result = mul(result,f);
// 	return result;
    }

    static final int AS1 = -1228;
    static final int AS2 = 4866;
    static final int AS3 = 13901;
    static final int AS4 = 102939;

    /** Compute asin(f)
        @param f FP value, 0 <= f <= 1
        @return the asin of f as a FP value
     */
    public static int asin (int f) {
        // todo: fix accuracy:
        return FP.floatToFP((float)Math.asin((double)FP.toFloat(f)));

// 	int fRoot = Sqrt((1<<16)-f);
// 	int result = AS1;
// 	result = mul(result, f);
// 	result += AS2;
// 	result = mul(result, f);
// 	result -= AS3;
// 	result = mul(result, f);
// 	result += AS4;
// 	result = PI_OVER_2 - (mul(fRoot,result));
// 	return result;
    }


    /** Compute acos(f)
        @param f FP value, 0 <= f <= 1
        @return the acos of f as a FP value
     */
    public static int acos (int f) {
        // todo: fix accuracy:
        return FP.floatToFP((float)Math.acos((double)FP.toFloat(f)));

// 	int fRoot = Sqrt((1<<16)-f);
// 	int result = AS1;
// 	result = mul(result, f);
// 	result += AS2;
// 	result = mul(result, f);
// 	result -= AS3;
// 	result = mul(result, f);
// 	result += AS4;
// 	result = mul(fRoot,result);
// 	return result;
    }


    /** Exponential

	e^x = 1 + x/1! + x^2/2! + x^3/3! + ...

    */

    static int fpfact[] = { 1<<16,
		     1<<16,
		     2<<16,
		     6<<16,
		     24<<16,
		     120<<16,
		     720<<16,
		     5040<<16,
		     40320<<16
    };


    /**
       Return the exponention of x
       @param x FP value
       @return FP value
    */
    public static int exp (int x) {
	int result = 1<<16;
	int x2 = mul(x,x);
	int x3 = mul(x2,x);
	int x4 = mul(x2,x2);
	int x5 = mul(x4,x);
	int x6 = mul(x4,x2);
	int x7 = mul(x6,x);
	int x8 = mul(x4,x4);
	return result + x 
	    + div(x2,fpfact[2]) 
	    + div(x3,fpfact[3]) 
	    + div(x4,fpfact[4])
	    + div(x5,fpfact[5]) 
	    + div(x6,fpfact[6]) 
	    + div(x7,fpfact[7])
	    + div(x8,fpfact[8]);
    }


    /** Logarithms: 
     * 
     * (2) Knuth, Donald E., "The Art of Computer Programming Vol 1",
     * Addison-Wesley Publishing Company, ISBN 0-201-03822-6 ( this
     * comes from Knuth (2), section 1.2.3, exercise 25).
     *
     * http://www.dattalo.com/technical/theory/logs.html
     *

    */

    /** This table is created using base of e. 
	
	(defun fixedpoint (z) 
	  (round (* z (lsh 1 16))))

	(loop for k from 0 to 16 do
	      (setq z (log (+ 1 (expt 2.0 (- (+ k 1)))))) 
	      (insert (format "%d\n"  (fixedpoint z))))


    */
    static int log2arr[] = {
	26573,
	14624,
	7719,
	3973,
	2017,
	1016,
	510,
	256,
	128,
	64,
	32,
	16,
	8,
	4,
	2,
	1,
	0,
	0,
	0
    };

    /*
      Binary Logarithm:

      case is very similar to the previous one. The only difference is
      in how the input is factored. Like before we are given:

      Input: 16 bit unsigned integer x; 0 < x < 65536

      (or 8 bit unsigned integer...)
      
      Output: g, lg(x) the logarithm of x with respect to base 2.
      
      3(b).i) Create a table of logarithms of the following constants:
      log2arr[i] = lg(1 + 2^(-(i+1))) 
      
      i = 0..M, M == desired size of the table.
      
      The first few values of the array are
      
      lg(3/2), lg(5/4), lg(9/8), lg(17/16),...
      
      Recall that in the previous case the factors were
      
      lg(2/1), lg(4/3), lg(8/7), lg(16/15),...
      
      Again, if you wish to compute logarithms to a different base,
      then substitute the lg() function with the appropriate based
      logarithm function.
      
      3(b).ii)Scale y to a value between 1 and 2.
      This is identical to 3(a).ii.
      
      3(b).iii) Changing Perspective
      Again, this is identical to 3(a).iii.
      
      3(b).iv) Factor y.
      
      This is very similar to step (iv) above. However, we now have
      different factors. Using the same example, x = 1.9, we can find
      the factors for this case.
      
      a) 1.9 > 1.5,
      x = x/1.5 ==> 1.266666
      b) 1.26 >  1.25
      x ==> 1.0133333
      c) 1.0133 < 1.125 so don't divide
      d) 1.0133 < 1.0625  "     "
      e) etc.
      
      
      So, x ~= 1.5 * 1.25 * etc.
      
      
      Like the previouse case, these factors are not perfect. Also,
      they're somewhat redundant in the sense that
      1.5*1.25*1.125*1.0625*... spans a range that is larger than 2 (
      ~2.38423 for i<=22). So unlike the previous factoring method,
      this one will not have repeated factors. Here's some psuedo
      code:
      
      for(i=1,d=0.5; i<M; i++, d/=2)
      if( x > 1+d)
      {
      x /= (1+d);
      g += log2arr[i-1];   // log2arr[i-1] = log2(1+d);
      }
      
      
      Here, d takes on the values of 0.5, 0.25, 0.125, ... , 2^(-i). Then
      1+d is the trial factor at each step. If x is greater than this trial
      factor, then we divide the trial factor out and add to g (ultimately
      the logarithm of x) the partial logarithm of the factor.
      
    */

    /*
	(loop for k from 0 to 16 do
	(setq z (log (expt 2 k)))
	(insert (format "%d,\n" (fixedpoint z))))
    */
    
    static int lnscale[] = {
	0,
	45426,
	90852,
	136278,
	181704,
	227130,
	272557,
	317983,
	363409,
	408835,
	454261,
	499687,
	545113,
	590539,
	635965,
	681391,
	726817
    };



    public static int Ln (int x) {
	// prescale so x is between 1 and 2
	int shift = 0;

	while (x > 1<<17) {
	    shift++;
	    x >>= 1;
	}

	int g = 0;
	int d = HALF;
	for (int i = 1; i < 16; i++) {
	    if (x > ((1<<16) + d)) {
		x = div(x, ( (1<<16) + d));
		g += log2arr[i-1];   // log2arr[i-1] = log2(1+d);
	    }
	    d >>= 1;
	}
	return g + lnscale[shift];
    }


    // The x,y point where two lines intersect
    public static int xIntersect;
    public static int yIntersect;

    /**
     * Does line segment A intersection line segment B?
     *
     * Assumes 16 bit fixed point numbers with 16 bits of fraction.
     *
     * For debugging, side effect xint, yint, the intersection point.
     *
     * <pre>
     * Algorithm 
     * 
     * As an example of algorithm development, consider the intersection of
     * two line segments.  Given line segment A goes from point XA1 and YA1
     * to point XA2 and YA2 and given line segment B goes from point XB1 and
     * YB1 to point XB2 and YB2.  Find whether there is zero, one, or an
     * infinite number of points of intersection (the line segments overlap)
     * and the values of the points of intersection.  Assume all numbers are
     * double.
     * 
     * For case 1 where line segment A is not vertical, line segment B is not
     * vertical, and line segment A is not parallel to line segment B, the
     * equations for line segment A and B are:
     * 
     * 
     * XMA = (YA2-YA1)/(XA2-XA1) = slope of line segment A
     * XBA = YA1 - XA1*XMA = Y-intercept for line segment A
     * YA = XMA*XA + XBA
     * 
     * XMB = (YB2-YB1)/(XB2-XB1) = slope of line segment B
     * XBB = YB1 - XB1*XMB = Y-intercept for line segment B
     * YB = XMB*XB + XBB
     * 
     * At the intersection of line segment A and B, XA=XB=XINT and YA=YB=YINT.
     * YINT = XMA*XINT + XBA
     * YINT = XMB*XINT + XBB
     * XMA*XINT + XBA = XMB*XINT + XBB
     * XMA*XINT - XMB*XINT = XBB - XBA
     * XINT*(XMA-XMB) = XBB - XBA
     * XINT = (XBB-XBA)/(XMA-XMB)
     * YINT = XMA*XINT + XBA
     * There is one point of intersection.
     * 
     * For case 2 where line segment A is vertical (XA1 is close to XA2) and
     * line segment B is not vertical, the equations for line segment A and B
     * are:
     * 
     * XA = 0.5*(XA1+XA2)
     * 
     * XMB = (YB2-YB1)/(XB2-XB1) = slope of line segment B
     * XBB = YB1 - XB1*XMB = Y-intercept for line segment B
     * YB = XMB*XB + XBB
     * 
     * At the intersection of line segment A and B, XA=XB=XINT and YA=YB=YINT.
     * XINT = XA
     * YINT = XMB*XINT + XBB
     * There is one point of intersection.
     * 
     * For case 3 where line segment A is not vertical and line segment B is
     * vertical (XB1 is close to XB2), the equations for line segment A and B
     * are:
     * 
     * XMA = (YA2-YA1)/(XA2-XA1) = slope of line segment A
     * XBA = YA1 - XA1*XMA = Y-intercept for line segment A
     * YA = XMA*XA + XBA
     * 
     * XB= 0.5*(XB1+XB2)
     * 
     * At the intersection of line segment A and B, XA=XB=XINT and YA=YB=YINT.
     * XINT = XB
     * YINT = XMA*XINT + XBA
     * There is one point of intersection.
     * 
     * For case 4 where line segment A is vertical (XA1 is close to XA2) and
     * line segment B is vertical (XB1 is close to XB2), the distance between
     * the parallel line segments is:
     * 
     * DIST = ABS ( 0.5*(XA1+XA2) - 0.5*(XB1+XB2) )
     * 
     * If DIST is close to zero, then:
     * 
     * XINT1 = 0.5*(0.5*(XA1+XA2)+0.5*(XB1+XB2))
     * YINT1 = MAX(MIN(YA1,YA2),MIN(YB1,YB2))
     * XINT2 = XINT1
     * YINT2 = MIN(MAX(YA1,YA2),MAX(YB1,YB2))
     * There are two points of intersection.
     * 
     * For case 5 where line segment A is not vertical, line segment B is not
     * vertical, and line segment A is parallel to line segment B (XMA is
     * close to XMB), the equations for line segment A and B are:
     * 
     * XMA = (YA2-YA1)/(XA2-XA1) = slope of line segment A
     * XBA = YA1 - XA1*XMA = Y-intercept for line segment A
     * YA = XMA*XA + XBA
     * 
     * XMB = (YB2-YB1)/(XB2-XB1) = slope of line segment B
     * XBB = YB1 - XB1*XMB = Y-intercept for line segment B
     * YB = XMB*XB + XBB
     * 
     * The distance between the parallel line segments is:
     * 
     * DIST = ABS(XBA-XBB)*COS(ATAN(0.5*(XMA+XMB)))
     * 
     * If DIST is close to zero, then:
     * 
     * XINT1 = MAX(MIN(XA1,XA2),MIN(XB1,XB2))
     * YINT1 = MAX(MIN(YA1,YA2),MIN(YB1,YB2))
     * XINT2 = MIN(MAX(XA1,XA2),MAX(XB1,XB2))
     * YINT2 = MIN(MAX(YA1,YA2),MAX(YB1,YB2))
     * There are two points of intersection.
     * 
     * After the point or points of intersection are calculated, each
     * solution must be checked to ensure that the point of intersection lies
     * on line segment A and B by checking if XINT >= MIN(XA1,XA2) and XINT
     * <= MAX(XA1,XA2) and YINT >= MIN(YA1,YA2) and YINT <= MAX(YA1,YA2) and
     * checking if XINT >= MIN(XB1,XB2) and XINT <= MAX(XB1,XB2) and YINT >=
     * MIN(XB1,XB2) and YINT <= MAX(YB1,YB2).
     * 
     * Note that case 2, 3, 4, and 5 are all special instances of case 1
     * where a division by zero would have caused the creation of an infinite
     * number and thus a program error.
     * 
     * </pre>
     */
    public static boolean intersects (int ax0, int ay0, int ax1, int ay1,
			int bx0, int by0, int bx1, int by1) {
	
	ax0 <<= 16;
	ay0 <<= 16;
	ax1 <<= 16;
	ay1 <<= 16;
	
	bx0 <<= 16;
	by0 <<= 16;
	bx1 <<= 16;
	by1 <<= 16;
	
	int adx = (ax1 - ax0);
	int ady = (ay1 - ay0);
	int bdx = (bx1 - bx0);
	int bdy = (by1 - by0);

	int xma;
	int xba;

	int xmb;
	int xbb;	
	int TWO = (2 << 16);

	if ((adx == 0) && (bdx == 0)) { // both vertical lines
	    int dist = Math.abs(div((ax0+ax1)-(bx0+bx1), TWO));
	    return (dist == 0);
	} else if (adx == 0) { // A  vertical
	    int xa = div((ax0 + ax1), TWO);
	    xmb = div(bdy,bdx);           // slope segment B
	    xbb = by0 - mul(bx0, xmb); // y intercept of segment B
	    xIntersect = xa;
	    yIntersect = (mul(xmb,xIntersect)) + xbb;
	} else if ( bdx == 0) { // B vertical
	    int xb = div((bx0+bx1), TWO);
	    xma = div(ady,adx);           // slope segment A
	    xba = ay0 - (mul(ax0,xma)); // y intercept of segment A
	    xIntersect = xb;
	    yIntersect = (mul(xma,xIntersect)) + xba;
	} else {
	     xma = div(ady,adx);           // slope segment A
	     xba = ay0 - (mul(ax0, xma)); // y intercept of segment A

	     xmb = div(bdy,bdx);           // slope segment B
	     xbb = by0 - (mul(bx0,xmb)); // y intercept of segment B
	
	     // parallel lines? 
	     if (xma == xmb) {
		 // Need trig functions
		 int dist = Math.abs(mul((xba-xbb),
                                         (cos(atan(div((xma+xmb), TWO))))));
		 if (dist < (1<<16) ) {
		     return true;
		 } else {
		     return false;
		 }
	     } else {
		 // Calculate points of intersection
		 // At the intersection of line segment A and B, XA=XB=XINT and YA=YB=YINT
		 if ((xma-xmb) == 0) {
		     return false;
		 }
		 xIntersect = div((xbb-xba),(xma-xmb));
		 yIntersect = (mul(xma,xIntersect)) + xba;
	     }
	}

	// After the point or points of intersection are calculated, each
	// solution must be checked to ensure that the point of intersection lies
	// on line segment A and B.
	
	int minxa = Math.min(ax0, ax1);
	int maxxa = Math.max(ax0, ax1);

	int minya = Math.min(ay0, ay1);
	int maxya = Math.max(ay0, ay1);

	int minxb = Math.min(bx0, bx1);
	int maxxb = Math.max(bx0, bx1);

	int minyb = Math.min(by0, by1);
	int maxyb = Math.max(by0, by1);

	return ((xIntersect >= minxa) && (xIntersect <= maxxa) && (yIntersect >= minya) && (yIntersect <= maxya) 
		&& 
		(xIntersect >= minxb) && (xIntersect <= maxxb) && (yIntersect >= minyb) && (yIntersect <= maxyb));
    }

}

