/**
 * Title: Force Field X.
 *
 * Description: Force Field X - Software for Molecular Biophysics.
 *
 * Copyright: Copyright (c) Michael J. Schnieders 2001-2017.
 *
 * This file is part of Force Field X.
 *
 * Force Field X is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * Force Field X is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Force Field X; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package ffx.numerics;

import org.apache.commons.math3.util.FastMath;

/**
 * A PowerSwitch interpolates between 0 and 1 vi f(x) = (ax)^beta, where x must 
 * be between 0 and 1/a.
 *
 * @author Jacob M. Litman
 * @author Michael J. Schnieders
 */
public class PowerSwitch implements UnivariateSwitchingFunction {

    private final double mult;
    private final double beta;
    private final double ub;
    
    public PowerSwitch() {
        this(1.0, 1.0);
    }
    
    public PowerSwitch(double mult, double beta) {
        if (mult <= 0) {
            throw new IllegalArgumentException("Multiplier must be > 0");
        }
        if (beta == 0) {
            throw new IllegalArgumentException("Exponent must be > 0, preferably >= 1");
        }
        this.mult = mult;
        this.beta = beta;
        ub = 1.0 / mult;
    }
    
    @Override
    public double getLowerBound() {
        return 0;
    }

    @Override
    public double getUpperBound() {
        return ub;
    }

    @Override
    public boolean constantOutsideBounds() {
        return false;
    }

    @Override
    public boolean validOutsideBounds() {
        return false;
    }

    @Override
    public int getHighestOrderZeroDerivative() {
        return beta > 0 ? ((int) beta) - 1 : 0;
    }
    
    @Override
    public boolean symmetricToUnity() {
        return (beta == 1.0);
    }

    @Override
    public double valueAt(double x) throws IllegalArgumentException {
        x *= mult;
        return FastMath.pow(x, beta);
    }

    @Override
    public double firstDerivative(double x) throws IllegalArgumentException {
        return beta * FastMath.pow(x, beta-1);
    }

    @Override
    public double secondDerivative(double x) throws IllegalArgumentException {
        return beta == 1.0 ? 0.0 : beta * (beta - 1) * FastMath.pow(x, beta - 2);
    }

    @Override
    public double nthDerivative(double x, int order) throws IllegalArgumentException {
        if (order < 1) {
            throw new IllegalArgumentException("Order must be >= 1");
        }
        switch (order) {
            case 1:
                return firstDerivative(x);
            case 2:
                return secondDerivative(x);
            default:
                double orderDiff = order - beta;
                if (orderDiff % 1.0 == 0 && orderDiff >= 1.0) {
                    return 0.0;
                } else {
                    double val = FastMath.pow(x, beta - order);
                    for (int i = 0; i < order; i++) {
                        val *= (beta - i);
                    }
                    return val;
                }
        }
    }
    
}