// ******************************************************************************
//
// Title:       Force Field X.
// Description: Force Field X - Software for Molecular Biophysics.
// Copyright:   Copyright (c) Michael J. Schnieders 2001-2021.
//
// This file is part of Force Field X.
//
// Force Field X is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License version 3 as published by
// the Free Software Foundation.
//
// Force Field X is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You should have received a copy of the GNU General Public License along with
// Force Field X; if not, write to the Free Software Foundation, Inc., 59 Temple
// Place, Suite 330, Boston, MA 02111-1307 USA
//
// Linking this library statically or dynamically with other modules is making a
// combined work based on this library. Thus, the terms and conditions of the
// GNU General Public License cover the whole combination.
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules, and
// to copy and distribute the resulting executable under terms of your choice,
// provided that you also meet, for each linked independent module, the terms
// and conditions of the license of that module. An independent module is a
// module which is not derived from or based on this library. If you modify this
// library, you may extend this exception to your version of the library, but
// you are not obligated to do so. If you do not wish to do so, delete this
// exception statement from your version.
//
// ******************************************************************************
package ffx.numerics.multipole;

import static java.lang.Math.fma;
import static java.lang.String.format;
import static org.apache.commons.math3.util.FastMath.sqrt;

/**
 * The CoulombTensorGlobal class computes derivatives of 1/|<b>r</b>| via recursion to arbitrary
 * order for Cartesian multipoles in the global frame.
 *
 * @author Michael J. Schnieders
 * @see <a href="http://doi.org/10.1142/9789812830364_0002" target="_blank"> Matt Challacombe, Eric
 *     Schwegler and Jan Almlof, Modern developments in Hartree-Fock theory: Fast methods for
 *     computing the Coulomb matrix. Computational Chemistry: Review of Current Trends. pp. 53-107,
 *     Ed. J. Leczszynski, World Scientifc, 1996. </a>
 * @since 1.0
 */
public class CoulombTensorGlobal extends MultipoleTensor {

  /**
   * Constructor for CoulombTensorGlobal.
   *
   * @param order a int.
   */
  public CoulombTensorGlobal(int order) {
    super(COORDINATES.GLOBAL, order);
    operator = OPERATOR.COULOMB;
  }

  /**
   * Generate source terms for the Coulomb Challacombe et al. recursion.
   *
   * @param T000 Location to store the source terms.
   */
  protected void source(double[] T000) {
    // Challacombe et al. Equation 21, last factor.
    // == (1/r) * (1/r^3) * (1/r^5) * (1/r^7) * ...
    double ir = 1.0 / R;
    double ir2 = ir * ir;
    for (int n = 0; n < o1; n++) {
      T000[n] = coulombSource[n] * ir;
      ir *= ir2;
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Meaningful only for QI.
   */
  @Override
  public double getd2EdZ2() {
    return 0.0;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Meaningful only for QI.
   */
  @Override
  public double getdEdZ() {
    return 0.0;
  }

  /** {@inheritDoc} */
  @Override
  protected void setR(double dx, double dy, double dz) {
    x = dx;
    y = dy;
    z = dz;
    r2 = (x * x + y * y + z * z);
    R = sqrt(r2);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void order2() {
    source(work);
    double term0000 = work[0];
    double term0001 = work[1];
    double term0002 = work[2];
    R000 = term0000;
    R100 = x * term0001;
    double term1001 = x * term0002;
    R200 = fma(x, term1001, term0001);
    R010 = y * term0001;
    double term0101 = y * term0002;
    R020 = fma(y, term0101, term0001);
    R110 = y * term1001;
    R001 = z * term0001;
    double term0011 = z * term0002;
    R002 = fma(z, term0011, term0001);
    R011 = z * term0101;
    R101 = z * term1001;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void order3() {
    source(work);
    double term0000 = work[0];
    double term0001 = work[1];
    double term0002 = work[2];
    double term0003 = work[3];
    R000 = term0000;
    R100 = x * term0001;
    double term1001 = x * term0002;
    R200 = fma(x, term1001, term0001);
    double term1002 = x * term0003;
    double term2001 = fma(x, term1002, term0002);
    R300 = fma(x, term2001, 2 * term1001);
    R010 = y * term0001;
    double term0101 = y * term0002;
    R020 = fma(y, term0101, term0001);
    double term0102 = y * term0003;
    double term0201 = fma(y, term0102, term0002);
    R030 = fma(y, term0201, 2 * term0101);
    R110 = y * term1001;
    double term1101 = y * term1002;
    R120 = fma(y, term1101, term1001);
    R210 = y * term2001;
    R001 = z * term0001;
    double term0011 = z * term0002;
    R002 = fma(z, term0011, term0001);
    double term0012 = z * term0003;
    double term0021 = fma(z, term0012, term0002);
    R003 = fma(z, term0021, 2 * term0011);
    R011 = z * term0101;
    double term0111 = z * term0102;
    R012 = fma(z, term0111, term0101);
    R021 = z * term0201;
    R101 = z * term1001;
    double term1011 = z * term1002;
    R102 = fma(z, term1011, term1001);
    R111 = z * term1101;
    R201 = z * term2001;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void order4() {
    source(work);
    double term0000 = work[0];
    double term0001 = work[1];
    double term0002 = work[2];
    double term0003 = work[3];
    double term0004 = work[4];
    R000 = term0000;
    R100 = x * term0001;
    double term1001 = x * term0002;
    R200 = fma(x, term1001, term0001);
    double term1002 = x * term0003;
    double term2001 = fma(x, term1002, term0002);
    R300 = fma(x, term2001, 2 * term1001);
    double term1003 = x * term0004;
    double term2002 = fma(x, term1003, term0003);
    double term3001 = fma(x, term2002, 2 * term1002);
    R400 = fma(x, term3001, 3 * term2001);
    R010 = y * term0001;
    double term0101 = y * term0002;
    R020 = fma(y, term0101, term0001);
    double term0102 = y * term0003;
    double term0201 = fma(y, term0102, term0002);
    R030 = fma(y, term0201, 2 * term0101);
    double term0103 = y * term0004;
    double term0202 = fma(y, term0103, term0003);
    double term0301 = fma(y, term0202, 2 * term0102);
    R040 = fma(y, term0301, 3 * term0201);
    R110 = y * term1001;
    double term1101 = y * term1002;
    R120 = fma(y, term1101, term1001);
    double term1102 = y * term1003;
    double term1201 = fma(y, term1102, term1002);
    R130 = fma(y, term1201, 2 * term1101);
    R210 = y * term2001;
    double term2101 = y * term2002;
    R220 = fma(y, term2101, term2001);
    R310 = y * term3001;
    R001 = z * term0001;
    double term0011 = z * term0002;
    R002 = fma(z, term0011, term0001);
    double term0012 = z * term0003;
    double term0021 = fma(z, term0012, term0002);
    R003 = fma(z, term0021, 2 * term0011);
    double term0013 = z * term0004;
    double term0022 = fma(z, term0013, term0003);
    double term0031 = fma(z, term0022, 2 * term0012);
    R004 = fma(z, term0031, 3 * term0021);
    R011 = z * term0101;
    double term0111 = z * term0102;
    R012 = fma(z, term0111, term0101);
    double term0112 = z * term0103;
    double term0121 = fma(z, term0112, term0102);
    R013 = fma(z, term0121, 2 * term0111);
    R021 = z * term0201;
    double term0211 = z * term0202;
    R022 = fma(z, term0211, term0201);
    R031 = z * term0301;
    R101 = z * term1001;
    double term1011 = z * term1002;
    R102 = fma(z, term1011, term1001);
    double term1012 = z * term1003;
    double term1021 = fma(z, term1012, term1002);
    R103 = fma(z, term1021, 2 * term1011);
    R111 = z * term1101;
    double term1111 = z * term1102;
    R112 = fma(z, term1111, term1101);
    R121 = z * term1201;
    R201 = z * term2001;
    double term2011 = z * term2002;
    R202 = fma(z, term2011, term2001);
    R211 = z * term2101;
    R301 = z * term3001;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void order5() {
    source(work);
    double term0000 = work[0];
    double term0001 = work[1];
    double term0002 = work[2];
    double term0003 = work[3];
    double term0004 = work[4];
    double term0005 = work[5];
    R000 = term0000;
    R100 = x * term0001;
    double term1001 = x * term0002;
    R200 = fma(x, term1001, term0001);
    double term1002 = x * term0003;
    double term2001 = fma(x, term1002, term0002);
    R300 = fma(x, term2001, 2 * term1001);
    double term1003 = x * term0004;
    double term2002 = fma(x, term1003, term0003);
    double term3001 = fma(x, term2002, 2 * term1002);
    R400 = fma(x, term3001, 3 * term2001);
    double term1004 = x * term0005;
    double term2003 = fma(x, term1004, term0004);
    double term3002 = fma(x, term2003, 2 * term1003);
    double term4001 = fma(x, term3002, 3 * term2002);
    R500 = fma(x, term4001, 4 * term3001);
    R010 = y * term0001;
    double term0101 = y * term0002;
    R020 = fma(y, term0101, term0001);
    double term0102 = y * term0003;
    double term0201 = fma(y, term0102, term0002);
    R030 = fma(y, term0201, 2 * term0101);
    double term0103 = y * term0004;
    double term0202 = fma(y, term0103, term0003);
    double term0301 = fma(y, term0202, 2 * term0102);
    R040 = fma(y, term0301, 3 * term0201);
    double term0104 = y * term0005;
    double term0203 = fma(y, term0104, term0004);
    double term0302 = fma(y, term0203, 2 * term0103);
    double term0401 = fma(y, term0302, 3 * term0202);
    R050 = fma(y, term0401, 4 * term0301);
    R110 = y * term1001;
    double term1101 = y * term1002;
    R120 = fma(y, term1101, term1001);
    double term1102 = y * term1003;
    double term1201 = fma(y, term1102, term1002);
    R130 = fma(y, term1201, 2 * term1101);
    double term1103 = y * term1004;
    double term1202 = fma(y, term1103, term1003);
    double term1301 = fma(y, term1202, 2 * term1102);
    R140 = fma(y, term1301, 3 * term1201);
    R210 = y * term2001;
    double term2101 = y * term2002;
    R220 = fma(y, term2101, term2001);
    double term2102 = y * term2003;
    double term2201 = fma(y, term2102, term2002);
    R230 = fma(y, term2201, 2 * term2101);
    R310 = y * term3001;
    double term3101 = y * term3002;
    R320 = fma(y, term3101, term3001);
    R410 = y * term4001;
    R001 = z * term0001;
    double term0011 = z * term0002;
    R002 = fma(z, term0011, term0001);
    double term0012 = z * term0003;
    double term0021 = fma(z, term0012, term0002);
    R003 = fma(z, term0021, 2 * term0011);
    double term0013 = z * term0004;
    double term0022 = fma(z, term0013, term0003);
    double term0031 = fma(z, term0022, 2 * term0012);
    R004 = fma(z, term0031, 3 * term0021);
    double term0014 = z * term0005;
    double term0023 = fma(z, term0014, term0004);
    double term0032 = fma(z, term0023, 2 * term0013);
    double term0041 = fma(z, term0032, 3 * term0022);
    R005 = fma(z, term0041, 4 * term0031);
    R011 = z * term0101;
    double term0111 = z * term0102;
    R012 = fma(z, term0111, term0101);
    double term0112 = z * term0103;
    double term0121 = fma(z, term0112, term0102);
    R013 = fma(z, term0121, 2 * term0111);
    double term0113 = z * term0104;
    double term0122 = fma(z, term0113, term0103);
    double term0131 = fma(z, term0122, 2 * term0112);
    R014 = fma(z, term0131, 3 * term0121);
    R021 = z * term0201;
    double term0211 = z * term0202;
    R022 = fma(z, term0211, term0201);
    double term0212 = z * term0203;
    double term0221 = fma(z, term0212, term0202);
    R023 = fma(z, term0221, 2 * term0211);
    R031 = z * term0301;
    double term0311 = z * term0302;
    R032 = fma(z, term0311, term0301);
    R041 = z * term0401;
    R101 = z * term1001;
    double term1011 = z * term1002;
    R102 = fma(z, term1011, term1001);
    double term1012 = z * term1003;
    double term1021 = fma(z, term1012, term1002);
    R103 = fma(z, term1021, 2 * term1011);
    double term1013 = z * term1004;
    double term1022 = fma(z, term1013, term1003);
    double term1031 = fma(z, term1022, 2 * term1012);
    R104 = fma(z, term1031, 3 * term1021);
    R111 = z * term1101;
    double term1111 = z * term1102;
    R112 = fma(z, term1111, term1101);
    double term1112 = z * term1103;
    double term1121 = fma(z, term1112, term1102);
    R113 = fma(z, term1121, 2 * term1111);
    R121 = z * term1201;
    double term1211 = z * term1202;
    R122 = fma(z, term1211, term1201);
    R131 = z * term1301;
    R201 = z * term2001;
    double term2011 = z * term2002;
    R202 = fma(z, term2011, term2001);
    double term2012 = z * term2003;
    double term2021 = fma(z, term2012, term2002);
    R203 = fma(z, term2021, 2 * term2011);
    R211 = z * term2101;
    double term2111 = z * term2102;
    R212 = fma(z, term2111, term2101);
    R221 = z * term2201;
    R301 = z * term3001;
    double term3011 = z * term3002;
    R302 = fma(z, term3011, term3001);
    R311 = z * term3101;
    R401 = z * term4001;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void order6() {
    source(work);
    double term0000 = work[0];
    double term0001 = work[1];
    double term0002 = work[2];
    double term0003 = work[3];
    double term0004 = work[4];
    double term0005 = work[5];
    double term0006 = work[6];
    R000 = term0000;
    R100 = x * term0001;
    double term1001 = x * term0002;
    R200 = fma(x, term1001, term0001);
    double term1002 = x * term0003;
    double term2001 = fma(x, term1002, term0002);
    R300 = fma(x, term2001, 2 * term1001);
    double term1003 = x * term0004;
    double term2002 = fma(x, term1003, term0003);
    double term3001 = fma(x, term2002, 2 * term1002);
    R400 = fma(x, term3001, 3 * term2001);
    double term1004 = x * term0005;
    double term2003 = fma(x, term1004, term0004);
    double term3002 = fma(x, term2003, 2 * term1003);
    double term4001 = fma(x, term3002, 3 * term2002);
    R500 = fma(x, term4001, 4 * term3001);
    double term1005 = x * term0006;
    double term2004 = fma(x, term1005, term0005);
    double term3003 = fma(x, term2004, 2 * term1004);
    double term4002 = fma(x, term3003, 3 * term2003);
    double term5001 = fma(x, term4002, 4 * term3002);
    R600 = fma(x, term5001, 5 * term4001);
    R010 = y * term0001;
    double term0101 = y * term0002;
    R020 = fma(y, term0101, term0001);
    double term0102 = y * term0003;
    double term0201 = fma(y, term0102, term0002);
    R030 = fma(y, term0201, 2 * term0101);
    double term0103 = y * term0004;
    double term0202 = fma(y, term0103, term0003);
    double term0301 = fma(y, term0202, 2 * term0102);
    R040 = fma(y, term0301, 3 * term0201);
    double term0104 = y * term0005;
    double term0203 = fma(y, term0104, term0004);
    double term0302 = fma(y, term0203, 2 * term0103);
    double term0401 = fma(y, term0302, 3 * term0202);
    R050 = fma(y, term0401, 4 * term0301);
    double term0105 = y * term0006;
    double term0204 = fma(y, term0105, term0005);
    double term0303 = fma(y, term0204, 2 * term0104);
    double term0402 = fma(y, term0303, 3 * term0203);
    double term0501 = fma(y, term0402, 4 * term0302);
    R060 = fma(y, term0501, 5 * term0401);
    R110 = y * term1001;
    double term1101 = y * term1002;
    R120 = fma(y, term1101, term1001);
    double term1102 = y * term1003;
    double term1201 = fma(y, term1102, term1002);
    R130 = fma(y, term1201, 2 * term1101);
    double term1103 = y * term1004;
    double term1202 = fma(y, term1103, term1003);
    double term1301 = fma(y, term1202, 2 * term1102);
    R140 = fma(y, term1301, 3 * term1201);
    double term1104 = y * term1005;
    double term1203 = fma(y, term1104, term1004);
    double term1302 = fma(y, term1203, 2 * term1103);
    double term1401 = fma(y, term1302, 3 * term1202);
    R150 = fma(y, term1401, 4 * term1301);
    R210 = y * term2001;
    double term2101 = y * term2002;
    R220 = fma(y, term2101, term2001);
    double term2102 = y * term2003;
    double term2201 = fma(y, term2102, term2002);
    R230 = fma(y, term2201, 2 * term2101);
    double term2103 = y * term2004;
    double term2202 = fma(y, term2103, term2003);
    double term2301 = fma(y, term2202, 2 * term2102);
    R240 = fma(y, term2301, 3 * term2201);
    R310 = y * term3001;
    double term3101 = y * term3002;
    R320 = fma(y, term3101, term3001);
    double term3102 = y * term3003;
    double term3201 = fma(y, term3102, term3002);
    R330 = fma(y, term3201, 2 * term3101);
    R410 = y * term4001;
    double term4101 = y * term4002;
    R420 = fma(y, term4101, term4001);
    R510 = y * term5001;
    R001 = z * term0001;
    double term0011 = z * term0002;
    R002 = fma(z, term0011, term0001);
    double term0012 = z * term0003;
    double term0021 = fma(z, term0012, term0002);
    R003 = fma(z, term0021, 2 * term0011);
    double term0013 = z * term0004;
    double term0022 = fma(z, term0013, term0003);
    double term0031 = fma(z, term0022, 2 * term0012);
    R004 = fma(z, term0031, 3 * term0021);
    double term0014 = z * term0005;
    double term0023 = fma(z, term0014, term0004);
    double term0032 = fma(z, term0023, 2 * term0013);
    double term0041 = fma(z, term0032, 3 * term0022);
    R005 = fma(z, term0041, 4 * term0031);
    double term0015 = z * term0006;
    double term0024 = fma(z, term0015, term0005);
    double term0033 = fma(z, term0024, 2 * term0014);
    double term0042 = fma(z, term0033, 3 * term0023);
    double term0051 = fma(z, term0042, 4 * term0032);
    R006 = fma(z, term0051, 5 * term0041);
    R011 = z * term0101;
    double term0111 = z * term0102;
    R012 = fma(z, term0111, term0101);
    double term0112 = z * term0103;
    double term0121 = fma(z, term0112, term0102);
    R013 = fma(z, term0121, 2 * term0111);
    double term0113 = z * term0104;
    double term0122 = fma(z, term0113, term0103);
    double term0131 = fma(z, term0122, 2 * term0112);
    R014 = fma(z, term0131, 3 * term0121);
    double term0114 = z * term0105;
    double term0123 = fma(z, term0114, term0104);
    double term0132 = fma(z, term0123, 2 * term0113);
    double term0141 = fma(z, term0132, 3 * term0122);
    R015 = fma(z, term0141, 4 * term0131);
    R021 = z * term0201;
    double term0211 = z * term0202;
    R022 = fma(z, term0211, term0201);
    double term0212 = z * term0203;
    double term0221 = fma(z, term0212, term0202);
    R023 = fma(z, term0221, 2 * term0211);
    double term0213 = z * term0204;
    double term0222 = fma(z, term0213, term0203);
    double term0231 = fma(z, term0222, 2 * term0212);
    R024 = fma(z, term0231, 3 * term0221);
    R031 = z * term0301;
    double term0311 = z * term0302;
    R032 = fma(z, term0311, term0301);
    double term0312 = z * term0303;
    double term0321 = fma(z, term0312, term0302);
    R033 = fma(z, term0321, 2 * term0311);
    R041 = z * term0401;
    double term0411 = z * term0402;
    R042 = fma(z, term0411, term0401);
    R051 = z * term0501;
    R101 = z * term1001;
    double term1011 = z * term1002;
    R102 = fma(z, term1011, term1001);
    double term1012 = z * term1003;
    double term1021 = fma(z, term1012, term1002);
    R103 = fma(z, term1021, 2 * term1011);
    double term1013 = z * term1004;
    double term1022 = fma(z, term1013, term1003);
    double term1031 = fma(z, term1022, 2 * term1012);
    R104 = fma(z, term1031, 3 * term1021);
    double term1014 = z * term1005;
    double term1023 = fma(z, term1014, term1004);
    double term1032 = fma(z, term1023, 2 * term1013);
    double term1041 = fma(z, term1032, 3 * term1022);
    R105 = fma(z, term1041, 4 * term1031);
    R111 = z * term1101;
    double term1111 = z * term1102;
    R112 = fma(z, term1111, term1101);
    double term1112 = z * term1103;
    double term1121 = fma(z, term1112, term1102);
    R113 = fma(z, term1121, 2 * term1111);
    double term1113 = z * term1104;
    double term1122 = fma(z, term1113, term1103);
    double term1131 = fma(z, term1122, 2 * term1112);
    R114 = fma(z, term1131, 3 * term1121);
    R121 = z * term1201;
    double term1211 = z * term1202;
    R122 = fma(z, term1211, term1201);
    double term1212 = z * term1203;
    double term1221 = fma(z, term1212, term1202);
    R123 = fma(z, term1221, 2 * term1211);
    R131 = z * term1301;
    double term1311 = z * term1302;
    R132 = fma(z, term1311, term1301);
    R141 = z * term1401;
    R201 = z * term2001;
    double term2011 = z * term2002;
    R202 = fma(z, term2011, term2001);
    double term2012 = z * term2003;
    double term2021 = fma(z, term2012, term2002);
    R203 = fma(z, term2021, 2 * term2011);
    double term2013 = z * term2004;
    double term2022 = fma(z, term2013, term2003);
    double term2031 = fma(z, term2022, 2 * term2012);
    R204 = fma(z, term2031, 3 * term2021);
    R211 = z * term2101;
    double term2111 = z * term2102;
    R212 = fma(z, term2111, term2101);
    double term2112 = z * term2103;
    double term2121 = fma(z, term2112, term2102);
    R213 = fma(z, term2121, 2 * term2111);
    R221 = z * term2201;
    double term2211 = z * term2202;
    R222 = fma(z, term2211, term2201);
    R231 = z * term2301;
    R301 = z * term3001;
    double term3011 = z * term3002;
    R302 = fma(z, term3011, term3001);
    double term3012 = z * term3003;
    double term3021 = fma(z, term3012, term3002);
    R303 = fma(z, term3021, 2 * term3011);
    R311 = z * term3101;
    double term3111 = z * term3102;
    R312 = fma(z, term3111, term3101);
    R321 = z * term3201;
    R401 = z * term4001;
    double term4011 = z * term4002;
    R402 = fma(z, term4011, term4001);
    R411 = z * term4101;
    R501 = z * term5001;
  }

  /** {@inheritDoc} */
  @Override
  protected void multipoleIPotentialAtK(PolarizableMultipole mI, int order) {
    // This is equation 3.1.3 in the Stone book.
    double term000 = 0.0;
    term000 = fma(mI.q, R000, term000);
    term000 = fma(mI.dx, -R100, term000);
    term000 = fma(mI.dy, -R010, term000);
    term000 = fma(mI.dz, -R001, term000);
    term000 = fma(mI.qxx, R200, term000);
    term000 = fma(mI.qyy, R020, term000);
    term000 = fma(mI.qzz, R002, term000);
    term000 = fma(mI.qxy, R110, term000);
    term000 = fma(mI.qxz, R101, term000);
    term000 = fma(mI.qyz, R011, term000);
    E000 = term000;
    if (order <= 0) {
      return;
    }
    // Order 1
    // This is d/dX of equation 3.1.3 in the Stone book.
    double term100 = 0.0;
    term100 = fma(mI.q, R100, term100);
    term100 = fma(mI.dx, -R200, term100);
    term100 = fma(mI.dy, -R110, term100);
    term100 = fma(mI.dz, -R101, term100);
    term100 = fma(mI.qxx, R300, term100);
    term100 = fma(mI.qyy, R120, term100);
    term100 = fma(mI.qzz, R102, term100);
    term100 = fma(mI.qxy, R210, term100);
    term100 = fma(mI.qxz, R201, term100);
    term100 = fma(mI.qyz, R111, term100);
    E100 = term100;
    // This is d/dY of equation 3.1.3 in the Stone book.
    double term010 = 0.0;
    term010 = fma(mI.q, R010, term010);
    term010 = fma(mI.dx, -R110, term010);
    term010 = fma(mI.dy, -R020, term010);
    term010 = fma(mI.dz, -R011, term010);
    term010 = fma(mI.qxx, R210, term010);
    term010 = fma(mI.qyy, R030, term010);
    term010 = fma(mI.qzz, R012, term010);
    term010 = fma(mI.qxy, R120, term010);
    term010 = fma(mI.qxz, R111, term010);
    term010 = fma(mI.qyz, R021, term010);
    E010 = term010;
    double term001 = 0.0;
    term001 = fma(mI.q, R001, term001);
    term001 = fma(mI.dx, -R101, term001);
    term001 = fma(mI.dy, -R011, term001);
    term001 = fma(mI.dz, -R002, term001);
    term001 = fma(mI.qxx, R201, term001);
    term001 = fma(mI.qyy, R021, term001);
    term001 = fma(mI.qzz, R003, term001);
    term001 = fma(mI.qxy, R111, term001);
    term001 = fma(mI.qxz, R102, term001);
    term001 = fma(mI.qyz, R012, term001);
    E001 = term001;
    if (order <= 1) {
      return;
    }
    // Order 2
    double term200 = 0.0;
    term200 = fma(mI.q, R200, term200);
    term200 = fma(mI.dx, -R300, term200);
    term200 = fma(mI.dy, -R210, term200);
    term200 = fma(mI.dz, -R201, term200);
    term200 = fma(mI.qxx, R400, term200);
    term200 = fma(mI.qyy, R220, term200);
    term200 = fma(mI.qzz, R202, term200);
    term200 = fma(mI.qxy, R310, term200);
    term200 = fma(mI.qxz, R301, term200);
    term200 = fma(mI.qyz, R211, term200);
    E200 = term200;
    double term020 = 0.0;
    term020 = fma(mI.q, R020, term020);
    term020 = fma(mI.dx, -R120, term020);
    term020 = fma(mI.dy, -R030, term020);
    term020 = fma(mI.dz, -R021, term020);
    term020 = fma(mI.qxx, R220, term020);
    term020 = fma(mI.qyy, R040, term020);
    term020 = fma(mI.qzz, R022, term020);
    term020 = fma(mI.qxy, R130, term020);
    term020 = fma(mI.qxz, R121, term020);
    term020 = fma(mI.qyz, R031, term020);
    E020 = term020;
    double term002 = 0.0;
    term002 = fma(mI.q, R002, term002);
    term002 = fma(mI.dx, -R102, term002);
    term002 = fma(mI.dy, -R012, term002);
    term002 = fma(mI.dz, -R003, term002);
    term002 = fma(mI.qxx, R202, term002);
    term002 = fma(mI.qyy, R022, term002);
    term002 = fma(mI.qzz, R004, term002);
    term002 = fma(mI.qxy, R112, term002);
    term002 = fma(mI.qxz, R103, term002);
    term002 = fma(mI.qyz, R013, term002);
    E002 = term002;
    double term110 = 0.0;
    term110 = fma(mI.q, R110, term110);
    term110 = fma(mI.dx, -R210, term110);
    term110 = fma(mI.dy, -R120, term110);
    term110 = fma(mI.dz, -R111, term110);
    term110 = fma(mI.qxx, R310, term110);
    term110 = fma(mI.qyy, R130, term110);
    term110 = fma(mI.qzz, R112, term110);
    term110 = fma(mI.qxy, R220, term110);
    term110 = fma(mI.qxz, R211, term110);
    term110 = fma(mI.qyz, R121, term110);
    E110 = term110;
    double term101 = 0.0;
    term101 = fma(mI.q, R101, term101);
    term101 = fma(mI.dx, -R201, term101);
    term101 = fma(mI.dy, -R111, term101);
    term101 = fma(mI.dz, -R102, term101);
    term101 = fma(mI.qxx, R301, term101);
    term101 = fma(mI.qyy, R121, term101);
    term101 = fma(mI.qzz, R103, term101);
    term101 = fma(mI.qxy, R211, term101);
    term101 = fma(mI.qxz, R202, term101);
    term101 = fma(mI.qyz, R112, term101);
    E101 = term101;
    double term011 = 0.0;
    term011 = fma(mI.q, R011, term011);
    term011 = fma(mI.dx, -R111, term011);
    term011 = fma(mI.dy, -R021, term011);
    term011 = fma(mI.dz, -R012, term011);
    term011 = fma(mI.qxx, R211, term011);
    term011 = fma(mI.qyy, R031, term011);
    term011 = fma(mI.qzz, R013, term011);
    term011 = fma(mI.qxy, R121, term011);
    term011 = fma(mI.qxz, R112, term011);
    term011 = fma(mI.qyz, R022, term011);
    E011 = term011;
    if (order <= 2) {
      return;
    }
    // Order 3
    double term300 = 0.0;
    term300 = fma(mI.q, R300, term300);
    term300 = fma(mI.dx, -R400, term300);
    term300 = fma(mI.dy, -R310, term300);
    term300 = fma(mI.dz, -R301, term300);
    term300 = fma(mI.qxx, R500, term300);
    term300 = fma(mI.qyy, R320, term300);
    term300 = fma(mI.qzz, R302, term300);
    term300 = fma(mI.qxy, R410, term300);
    term300 = fma(mI.qxz, R401, term300);
    term300 = fma(mI.qyz, R311, term300);
    E300 = term300;
    double term030 = 0.0;
    term030 = fma(mI.q, R030, term030);
    term030 = fma(mI.dx, -R130, term030);
    term030 = fma(mI.dy, -R040, term030);
    term030 = fma(mI.dz, -R031, term030);
    term030 = fma(mI.qxx, R230, term030);
    term030 = fma(mI.qyy, R050, term030);
    term030 = fma(mI.qzz, R032, term030);
    term030 = fma(mI.qxy, R140, term030);
    term030 = fma(mI.qxz, R131, term030);
    term030 = fma(mI.qyz, R041, term030);
    E030 = term030;
    double term003 = 0.0;
    term003 = fma(mI.q, R003, term003);
    term003 = fma(mI.dx, -R103, term003);
    term003 = fma(mI.dy, -R013, term003);
    term003 = fma(mI.dz, -R004, term003);
    term003 = fma(mI.qxx, R203, term003);
    term003 = fma(mI.qyy, R023, term003);
    term003 = fma(mI.qzz, R005, term003);
    term003 = fma(mI.qxy, R113, term003);
    term003 = fma(mI.qxz, R104, term003);
    term003 = fma(mI.qyz, R014, term003);
    E003 = term003;
    double term210 = 0.0;
    term210 = fma(mI.q, R210, term210);
    term210 = fma(mI.dx, -R310, term210);
    term210 = fma(mI.dy, -R220, term210);
    term210 = fma(mI.dz, -R211, term210);
    term210 = fma(mI.qxx, R410, term210);
    term210 = fma(mI.qyy, R230, term210);
    term210 = fma(mI.qzz, R212, term210);
    term210 = fma(mI.qxy, R320, term210);
    term210 = fma(mI.qxz, R311, term210);
    term210 = fma(mI.qyz, R221, term210);
    E210 = term210;
    double term201 = 0.0;
    term201 = fma(mI.q, R201, term201);
    term201 = fma(mI.dx, -R301, term201);
    term201 = fma(mI.dy, -R211, term201);
    term201 = fma(mI.dz, -R202, term201);
    term201 = fma(mI.qxx, R401, term201);
    term201 = fma(mI.qyy, R221, term201);
    term201 = fma(mI.qzz, R203, term201);
    term201 = fma(mI.qxy, R311, term201);
    term201 = fma(mI.qxz, R302, term201);
    term201 = fma(mI.qyz, R212, term201);
    E201 = term201;
    double term120 = 0.0;
    term120 = fma(mI.q, R120, term120);
    term120 = fma(mI.dx, -R220, term120);
    term120 = fma(mI.dy, -R130, term120);
    term120 = fma(mI.dz, -R121, term120);
    term120 = fma(mI.qxx, R320, term120);
    term120 = fma(mI.qyy, R140, term120);
    term120 = fma(mI.qzz, R122, term120);
    term120 = fma(mI.qxy, R230, term120);
    term120 = fma(mI.qxz, R221, term120);
    term120 = fma(mI.qyz, R131, term120);
    E120 = term120;
    double term021 = 0.0;
    term021 = fma(mI.q, R021, term021);
    term021 = fma(mI.dx, -R121, term021);
    term021 = fma(mI.dy, -R031, term021);
    term021 = fma(mI.dz, -R022, term021);
    term021 = fma(mI.qxx, R221, term021);
    term021 = fma(mI.qyy, R041, term021);
    term021 = fma(mI.qzz, R023, term021);
    term021 = fma(mI.qxy, R131, term021);
    term021 = fma(mI.qxz, R122, term021);
    term021 = fma(mI.qyz, R032, term021);
    E021 = term021;
    double term102 = 0.0;
    term102 = fma(mI.q, R102, term102);
    term102 = fma(mI.dx, -R202, term102);
    term102 = fma(mI.dy, -R112, term102);
    term102 = fma(mI.dz, -R103, term102);
    term102 = fma(mI.qxx, R302, term102);
    term102 = fma(mI.qyy, R122, term102);
    term102 = fma(mI.qzz, R104, term102);
    term102 = fma(mI.qxy, R212, term102);
    term102 = fma(mI.qxz, R203, term102);
    term102 = fma(mI.qyz, R113, term102);
    E102 = term102;
    double term012 = 0.0;
    term012 = fma(mI.q, R012, term012);
    term012 = fma(mI.dx, -R112, term012);
    term012 = fma(mI.dy, -R022, term012);
    term012 = fma(mI.dz, -R013, term012);
    term012 = fma(mI.qxx, R212, term012);
    term012 = fma(mI.qyy, R032, term012);
    term012 = fma(mI.qzz, R014, term012);
    term012 = fma(mI.qxy, R122, term012);
    term012 = fma(mI.qxz, R113, term012);
    term012 = fma(mI.qyz, R023, term012);
    E012 = term012;
    double term111 = 0.0;
    term111 = fma(mI.q, R111, term111);
    term111 = fma(mI.dx, -R211, term111);
    term111 = fma(mI.dy, -R121, term111);
    term111 = fma(mI.dz, -R112, term111);
    term111 = fma(mI.qxx, R311, term111);
    term111 = fma(mI.qyy, R131, term111);
    term111 = fma(mI.qzz, R113, term111);
    term111 = fma(mI.qxy, R221, term111);
    term111 = fma(mI.qxz, R212, term111);
    term111 = fma(mI.qyz, R122, term111);
    E111 = term111;
  }

  /** {@inheritDoc} */
  @Override
  protected void multipoleKPotentialAtI(PolarizableMultipole mK, int order) {
    // This is equation 3.1.3 in the Stone book, except its V_B at A.
    // The sign for separation vector is reversed, so the dipole contribution becomes positive.
    double term000 = 0.0;
    term000 = fma(mK.q, R000, term000);
    term000 = fma(mK.dx, R100, term000);
    term000 = fma(mK.dy, R010, term000);
    term000 = fma(mK.dz, R001, term000);
    term000 = fma(mK.qxx, R200, term000);
    term000 = fma(mK.qyy, R020, term000);
    term000 = fma(mK.qzz, R002, term000);
    term000 = fma(mK.qxy, R110, term000);
    term000 = fma(mK.qxz, R101, term000);
    term000 = fma(mK.qyz, R011, term000);
    E000 = term000;
    if (order <= 0) {
      return;
    }
    // This is d/dX of equation 3.1.3 in the Stone book. The sign is flipped due to the
    // derivative being with respect to R = Rk - Ri.
    double term100 = 0.0;
    term100 = fma(mK.q, R100, term100);
    term100 = fma(mK.dx, R200, term100);
    term100 = fma(mK.dy, R110, term100);
    term100 = fma(mK.dz, R101, term100);
    term100 = fma(mK.qxx, R300, term100);
    term100 = fma(mK.qyy, R120, term100);
    term100 = fma(mK.qzz, R102, term100);
    term100 = fma(mK.qxy, R210, term100);
    term100 = fma(mK.qxz, R201, term100);
    term100 = fma(mK.qyz, R111, term100);
    E100 = -term100;
    double term010 = 0.0;
    term010 = fma(mK.q, R010, term010);
    term010 = fma(mK.dx, R110, term010);
    term010 = fma(mK.dy, R020, term010);
    term010 = fma(mK.dz, R011, term010);
    term010 = fma(mK.qxx, R210, term010);
    term010 = fma(mK.qyy, R030, term010);
    term010 = fma(mK.qzz, R012, term010);
    term010 = fma(mK.qxy, R120, term010);
    term010 = fma(mK.qxz, R111, term010);
    term010 = fma(mK.qyz, R021, term010);
    E010 = -term010;
    double term001 = 0.0;
    term001 = fma(mK.q, R001, term001);
    term001 = fma(mK.dx, R101, term001);
    term001 = fma(mK.dy, R011, term001);
    term001 = fma(mK.dz, R002, term001);
    term001 = fma(mK.qxx, R201, term001);
    term001 = fma(mK.qyy, R021, term001);
    term001 = fma(mK.qzz, R003, term001);
    term001 = fma(mK.qxy, R111, term001);
    term001 = fma(mK.qxz, R102, term001);
    term001 = fma(mK.qyz, R012, term001);
    E001 = -term001;
    if (order <= 1) {
      return;
    }
    // Order 2
    double term200 = 0.0;
    term200 = fma(mK.q, R200, term200);
    term200 = fma(mK.dx, R300, term200);
    term200 = fma(mK.dy, R210, term200);
    term200 = fma(mK.dz, R201, term200);
    term200 = fma(mK.qxx, R400, term200);
    term200 = fma(mK.qyy, R220, term200);
    term200 = fma(mK.qzz, R202, term200);
    term200 = fma(mK.qxy, R310, term200);
    term200 = fma(mK.qxz, R301, term200);
    term200 = fma(mK.qyz, R211, term200);
    E200 = term200;
    double term020 = 0.0;
    term020 = fma(mK.q, R020, term020);
    term020 = fma(mK.dx, R120, term020);
    term020 = fma(mK.dy, R030, term020);
    term020 = fma(mK.dz, R021, term020);
    term020 = fma(mK.qxx, R220, term020);
    term020 = fma(mK.qyy, R040, term020);
    term020 = fma(mK.qzz, R022, term020);
    term020 = fma(mK.qxy, R130, term020);
    term020 = fma(mK.qxz, R121, term020);
    term020 = fma(mK.qyz, R031, term020);
    E020 = term020;
    double term002 = 0.0;
    term002 = fma(mK.q, R002, term002);
    term002 = fma(mK.dx, R102, term002);
    term002 = fma(mK.dy, R012, term002);
    term002 = fma(mK.dz, R003, term002);
    term002 = fma(mK.qxx, R202, term002);
    term002 = fma(mK.qyy, R022, term002);
    term002 = fma(mK.qzz, R004, term002);
    term002 = fma(mK.qxy, R112, term002);
    term002 = fma(mK.qxz, R103, term002);
    term002 = fma(mK.qyz, R013, term002);
    E002 = term002;
    double term110 = 0.0;
    term110 = fma(mK.q, R110, term110);
    term110 = fma(mK.dx, R210, term110);
    term110 = fma(mK.dy, R120, term110);
    term110 = fma(mK.dz, R111, term110);
    term110 = fma(mK.qxx, R310, term110);
    term110 = fma(mK.qyy, R130, term110);
    term110 = fma(mK.qzz, R112, term110);
    term110 = fma(mK.qxy, R220, term110);
    term110 = fma(mK.qxz, R211, term110);
    term110 = fma(mK.qyz, R121, term110);
    E110 = term110;
    double term101 = 0.0;
    term101 = fma(mK.q, R101, term101);
    term101 = fma(mK.dx, R201, term101);
    term101 = fma(mK.dy, R111, term101);
    term101 = fma(mK.dz, R102, term101);
    term101 = fma(mK.qxx, R301, term101);
    term101 = fma(mK.qyy, R121, term101);
    term101 = fma(mK.qzz, R103, term101);
    term101 = fma(mK.qxy, R211, term101);
    term101 = fma(mK.qxz, R202, term101);
    term101 = fma(mK.qyz, R112, term101);
    E101 = term101;
    double term011 = 0.0;
    term011 = fma(mK.q, R011, term011);
    term011 = fma(mK.dx, R111, term011);
    term011 = fma(mK.dy, R021, term011);
    term011 = fma(mK.dz, R012, term011);
    term011 = fma(mK.qxx, R211, term011);
    term011 = fma(mK.qyy, R031, term011);
    term011 = fma(mK.qzz, R013, term011);
    term011 = fma(mK.qxy, R121, term011);
    term011 = fma(mK.qxz, R112, term011);
    term011 = fma(mK.qyz, R022, term011);
    E011 = term011;
    if (order <= 2) {
      return;
    }
    // Order 3
    // This is d^3/dX^3 of equation 3.1.3 in the Stone book. The sign is flipped due to the
    // derivative being with respect to R = Rk - Ri.
    double term300 = 0.0;
    term300 = fma(mK.q, R300, term300);
    term300 = fma(mK.dx, R400, term300);
    term300 = fma(mK.dy, R310, term300);
    term300 = fma(mK.dz, R301, term300);
    term300 = fma(mK.qxx, R500, term300);
    term300 = fma(mK.qyy, R320, term300);
    term300 = fma(mK.qzz, R302, term300);
    term300 = fma(mK.qxy, R410, term300);
    term300 = fma(mK.qxz, R401, term300);
    term300 = fma(mK.qyz, R311, term300);
    E300 = -term300;
    double term030 = 0.0;
    term030 = fma(mK.q, R030, term030);
    term030 = fma(mK.dx, R130, term030);
    term030 = fma(mK.dy, R040, term030);
    term030 = fma(mK.dz, R031, term030);
    term030 = fma(mK.qxx, R230, term030);
    term030 = fma(mK.qyy, R050, term030);
    term030 = fma(mK.qzz, R032, term030);
    term030 = fma(mK.qxy, R140, term030);
    term030 = fma(mK.qxz, R131, term030);
    term030 = fma(mK.qyz, R041, term030);
    E030 = -term030;
    double term003 = 0.0;
    term003 = fma(mK.q, R003, term003);
    term003 = fma(mK.dx, R103, term003);
    term003 = fma(mK.dy, R013, term003);
    term003 = fma(mK.dz, R004, term003);
    term003 = fma(mK.qxx, R203, term003);
    term003 = fma(mK.qyy, R023, term003);
    term003 = fma(mK.qzz, R005, term003);
    term003 = fma(mK.qxy, R113, term003);
    term003 = fma(mK.qxz, R104, term003);
    term003 = fma(mK.qyz, R014, term003);
    E003 = -term003;
    double term210 = 0.0;
    term210 = fma(mK.q, R210, term210);
    term210 = fma(mK.dx, R310, term210);
    term210 = fma(mK.dy, R220, term210);
    term210 = fma(mK.dz, R211, term210);
    term210 = fma(mK.qxx, R410, term210);
    term210 = fma(mK.qyy, R230, term210);
    term210 = fma(mK.qzz, R212, term210);
    term210 = fma(mK.qxy, R320, term210);
    term210 = fma(mK.qxz, R311, term210);
    term210 = fma(mK.qyz, R221, term210);
    E210 = -term210;
    double term201 = 0.0;
    term201 = fma(mK.q, R201, term201);
    term201 = fma(mK.dx, R301, term201);
    term201 = fma(mK.dy, R211, term201);
    term201 = fma(mK.dz, R202, term201);
    term201 = fma(mK.qxx, R401, term201);
    term201 = fma(mK.qyy, R221, term201);
    term201 = fma(mK.qzz, R203, term201);
    term201 = fma(mK.qxy, R311, term201);
    term201 = fma(mK.qxz, R302, term201);
    term201 = fma(mK.qyz, R212, term201);
    E201 = -term201;
    double term120 = 0.0;
    term120 = fma(mK.q, R120, term120);
    term120 = fma(mK.dx, R220, term120);
    term120 = fma(mK.dy, R130, term120);
    term120 = fma(mK.dz, R121, term120);
    term120 = fma(mK.qxx, R320, term120);
    term120 = fma(mK.qyy, R140, term120);
    term120 = fma(mK.qzz, R122, term120);
    term120 = fma(mK.qxy, R230, term120);
    term120 = fma(mK.qxz, R221, term120);
    term120 = fma(mK.qyz, R131, term120);
    E120 = -term120;
    double term021 = 0.0;
    term021 = fma(mK.q, R021, term021);
    term021 = fma(mK.dx, R121, term021);
    term021 = fma(mK.dy, R031, term021);
    term021 = fma(mK.dz, R022, term021);
    term021 = fma(mK.qxx, R221, term021);
    term021 = fma(mK.qyy, R041, term021);
    term021 = fma(mK.qzz, R023, term021);
    term021 = fma(mK.qxy, R131, term021);
    term021 = fma(mK.qxz, R122, term021);
    term021 = fma(mK.qyz, R032, term021);
    E021 = -term021;
    double term102 = 0.0;
    term102 = fma(mK.q, R102, term102);
    term102 = fma(mK.dx, R202, term102);
    term102 = fma(mK.dy, R112, term102);
    term102 = fma(mK.dz, R103, term102);
    term102 = fma(mK.qxx, R302, term102);
    term102 = fma(mK.qyy, R122, term102);
    term102 = fma(mK.qzz, R104, term102);
    term102 = fma(mK.qxy, R212, term102);
    term102 = fma(mK.qxz, R203, term102);
    term102 = fma(mK.qyz, R113, term102);
    E102 = -term102;
    double term012 = 0.0;
    term012 = fma(mK.q, R012, term012);
    term012 = fma(mK.dx, R112, term012);
    term012 = fma(mK.dy, R022, term012);
    term012 = fma(mK.dz, R013, term012);
    term012 = fma(mK.qxx, R212, term012);
    term012 = fma(mK.qyy, R032, term012);
    term012 = fma(mK.qzz, R014, term012);
    term012 = fma(mK.qxy, R122, term012);
    term012 = fma(mK.qxz, R113, term012);
    term012 = fma(mK.qyz, R023, term012);
    E012 = -term012;
    double term111 = 0.0;
    term111 = fma(mK.q, R111, term111);
    term111 = fma(mK.dx, R211, term111);
    term111 = fma(mK.dy, R121, term111);
    term111 = fma(mK.dz, R112, term111);
    term111 = fma(mK.qxx, R311, term111);
    term111 = fma(mK.qyy, R131, term111);
    term111 = fma(mK.qzz, R113, term111);
    term111 = fma(mK.qxy, R221, term111);
    term111 = fma(mK.qxz, R212, term111);
    term111 = fma(mK.qyz, R122, term111);
    E111 = -term111;
  }

  /** {@inheritDoc} */
  @Override
  protected void inducedIPotentialAtK(PolarizableMultipole mI) {
    double term000 = -mI.ux * R100;
    term000 -= mI.uy * R010;
    term000 -= mI.uz * R001;
    E000 = term000;
    double term100 = -mI.ux * R200;
    term100 -= mI.uy * R110;
    term100 -= mI.uz * R101;
    E100 = term100;
    double term010 = -mI.ux * R110;
    term010 -= mI.uy * R020;
    term010 -= mI.uz * R011;
    E010 = term010;
    double term001 = -mI.ux * R101;
    term001 -= mI.uy * R011;
    term001 -= mI.uz * R002;
    E001 = term001;
    double term200 = -mI.ux * R300;
    term200 -= mI.uy * R210;
    term200 -= mI.uz * R201;
    E200 = term200;
    double term020 = -mI.ux * R120;
    term020 -= mI.uy * R030;
    term020 -= mI.uz * R021;
    E020 = term020;
    double term002 = -mI.ux * R102;
    term002 -= mI.uy * R012;
    term002 -= mI.uz * R003;
    E002 = term002;
    double term110 = -mI.ux * R210;
    term110 -= mI.uy * R120;
    term110 -= mI.uz * R111;
    E110 = term110;
    double term101 = -mI.ux * R201;
    term101 -= mI.uy * R111;
    term101 -= mI.uz * R102;
    E101 = term101;
    double term011 = -mI.ux * R111;
    term011 -= mI.uy * R021;
    term011 -= mI.uz * R012;
    E011 = term011;
  }

  /** {@inheritDoc} */
  @Override
  protected void inducedICRPotentialAtK(PolarizableMultipole mI) {
    double term000 = -mI.px * R100;
    term000 -= mI.py * R010;
    term000 -= mI.pz * R001;
    E000 = term000;
    double term100 = -mI.px * R200;
    term100 -= mI.py * R110;
    term100 -= mI.pz * R101;
    E100 = term100;
    double term010 = -mI.px * R110;
    term010 -= mI.py * R020;
    term010 -= mI.pz * R011;
    E010 = term010;
    double term001 = -mI.px * R101;
    term001 -= mI.py * R011;
    term001 -= mI.pz * R002;
    E001 = term001;
    double term200 = -mI.px * R300;
    term200 -= mI.py * R210;
    term200 -= mI.pz * R201;
    E200 = term200;
    double term020 = -mI.px * R120;
    term020 -= mI.py * R030;
    term020 -= mI.pz * R021;
    E020 = term020;
    double term002 = -mI.px * R102;
    term002 -= mI.py * R012;
    term002 -= mI.pz * R003;
    E002 = term002;
    double term110 = -mI.px * R210;
    term110 -= mI.py * R120;
    term110 -= mI.pz * R111;
    E110 = term110;
    double term101 = -mI.px * R201;
    term101 -= mI.py * R111;
    term101 -= mI.pz * R102;
    E101 = term101;
    double term011 = -mI.px * R111;
    term011 -= mI.py * R021;
    term011 -= mI.pz * R012;
    E011 = term011;
  }

  /** {@inheritDoc} */
  @Override
  protected void inducedITotalPotentialAtK(PolarizableMultipole mI) {
    double term000 = -mI.sx * R100;
    term000 -= mI.sy * R010;
    term000 -= mI.sz * R001;
    E000 = term000;
    double term100 = -mI.sx * R200;
    term100 -= mI.sy * R110;
    term100 -= mI.sz * R101;
    E100 = term100;
    double term010 = -mI.sx * R110;
    term010 -= mI.sy * R020;
    term010 -= mI.sz * R011;
    E010 = term010;
    double term001 = -mI.sx * R101;
    term001 -= mI.sy * R011;
    term001 -= mI.sz * R002;
    E001 = term001;
    double term200 = -mI.sx * R300;
    term200 -= mI.sy * R210;
    term200 -= mI.sz * R201;
    E200 = term200;
    double term020 = -mI.sx * R120;
    term020 -= mI.sy * R030;
    term020 -= mI.sz * R021;
    E020 = term020;
    double term002 = -mI.sx * R102;
    term002 -= mI.sy * R012;
    term002 -= mI.sz * R003;
    E002 = term002;
    double term110 = -mI.sx * R210;
    term110 -= mI.sy * R120;
    term110 -= mI.sz * R111;
    E110 = term110;
    double term101 = -mI.sx * R201;
    term101 -= mI.sy * R111;
    term101 -= mI.sz * R102;
    E101 = term101;
    double term011 = -mI.sx * R111;
    term011 -= mI.sy * R021;
    term011 -= mI.sz * R012;
    E011 = term011;
  }

  /** {@inheritDoc} */
  @Override
  protected void inducedKPotentialAtI(PolarizableMultipole mK) {
    double term000 = mK.ux * R100;
    term000 += mK.uy * R010;
    term000 += mK.uz * R001;
    E000 = term000;
    double term100 = mK.ux * R200;
    term100 += mK.uy * R110;
    term100 += mK.uz * R101;
    E100 = -term100;
    double term010 = mK.ux * R110;
    term010 += mK.uy * R020;
    term010 += mK.uz * R011;
    E010 = -term010;
    double term001 = mK.ux * R101;
    term001 += mK.uy * R011;
    term001 += mK.uz * R002;
    E001 = -term001;
    double term200 = mK.ux * R300;
    term200 += mK.uy * R210;
    term200 += mK.uz * R201;
    E200 = term200;
    double term020 = mK.ux * R120;
    term020 += mK.uy * R030;
    term020 += mK.uz * R021;
    E020 = term020;
    double term002 = mK.ux * R102;
    term002 += mK.uy * R012;
    term002 += mK.uz * R003;
    E002 = term002;
    double term110 = mK.ux * R210;
    term110 += mK.uy * R120;
    term110 += mK.uz * R111;
    E110 = term110;
    double term101 = mK.ux * R201;
    term101 += mK.uy * R111;
    term101 += mK.uz * R102;
    E101 = term101;
    double term011 = mK.ux * R111;
    term011 += mK.uy * R021;
    term011 += mK.uz * R012;
    E011 = term011;
  }

  /** {@inheritDoc} */
  @Override
  protected void inducedKCRPotentialAtI(PolarizableMultipole mK) {
    double term000 = mK.px * R100;
    term000 += mK.py * R010;
    term000 += mK.pz * R001;
    E000 = term000;
    double term100 = mK.px * R200;
    term100 += mK.py * R110;
    term100 += mK.pz * R101;
    E100 = -term100;
    double term010 = mK.px * R110;
    term010 += mK.py * R020;
    term010 += mK.pz * R011;
    E010 = -term010;
    double term001 = mK.px * R101;
    term001 += mK.py * R011;
    term001 += mK.pz * R002;
    E001 = -term001;
    double term200 = mK.px * R300;
    term200 += mK.py * R210;
    term200 += mK.pz * R201;
    E200 = term200;
    double term020 = mK.px * R120;
    term020 += mK.py * R030;
    term020 += mK.pz * R021;
    E020 = term020;
    double term002 = mK.px * R102;
    term002 += mK.py * R012;
    term002 += mK.pz * R003;
    E002 = term002;
    double term110 = mK.px * R210;
    term110 += mK.py * R120;
    term110 += mK.pz * R111;
    E110 = term110;
    double term101 = mK.px * R201;
    term101 += mK.py * R111;
    term101 += mK.pz * R102;
    E101 = term101;
    double term011 = mK.px * R111;
    term011 += mK.py * R021;
    term011 += mK.pz * R012;
    E011 = term011;
  }

  /** {@inheritDoc} */
  @Override
  protected void inducedKTotalPotentialAtI(PolarizableMultipole mK) {
    double term000 = mK.sx * R100;
    term000 += mK.sy * R010;
    term000 += mK.sz * R001;
    E000 = term000;
    double term100 = mK.sx * R200;
    term100 += mK.sy * R110;
    term100 += mK.sz * R101;
    E100 = -term100;
    double term010 = mK.sx * R110;
    term010 += mK.sy * R020;
    term010 += mK.sz * R011;
    E010 = -term010;
    double term001 = mK.sx * R101;
    term001 += mK.sy * R011;
    term001 += mK.sz * R002;
    E001 = -term001;
    double term200 = mK.sx * R300;
    term200 += mK.sy * R210;
    term200 += mK.sz * R201;
    E200 = term200;
    double term020 = mK.sx * R120;
    term020 += mK.sy * R030;
    term020 += mK.sz * R021;
    E020 = term020;
    double term002 = mK.sx * R102;
    term002 += mK.sy * R012;
    term002 += mK.sz * R003;
    E002 = term002;
    double term110 = mK.sx * R210;
    term110 += mK.sy * R120;
    term110 += mK.sz * R111;
    E110 = term110;
    double term101 = mK.sx * R201;
    term101 += mK.sy * R111;
    term101 += mK.sz * R102;
    E101 = term101;
    double term011 = mK.sx * R111;
    term011 += mK.sy * R021;
    term011 += mK.sz * R012;
    E011 = term011;
  }

  /** {@inheritDoc} */
  @Override
  protected double Tlmnj(
      final int l, final int m, final int n, final int j, final double[] r, final double[] T000) {
    if (m == 0 && n == 0) {
      if (l > 1) {
        return r[0] * Tlmnj(l - 1, 0, 0, j + 1, r, T000)
            + (l - 1) * Tlmnj(l - 2, 0, 0, j + 1, r, T000);
      } else if (l == 1) { // l == 1; d/dx is done.
        return r[0] * Tlmnj(0, 0, 0, j + 1, r, T000);
      } else {
        // l = m = n = 0; Recursion is done.
        return T000[j];
      }
    } else if (n == 0) {
      // m >= 1
      if (m > 1) {
        return r[1] * Tlmnj(l, m - 1, 0, j + 1, r, T000)
            + (m - 1) * Tlmnj(l, m - 2, 0, j + 1, r, T000);
      }
      // m == 1; d/dy is done.
      return r[1] * Tlmnj(l, 0, 0, j + 1, r, T000);
    } else {
      // n >= 1
      if (n > 1) {
        return r[2] * Tlmnj(l, m, n - 1, j + 1, r, T000)
            + (n - 1) * Tlmnj(l, m, n - 2, j + 1, r, T000);
      }
      // n == 1; d/dz is done.
      return r[2] * Tlmnj(l, m, 0, j + 1, r, T000);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>This method is a driver to collect elements of the Cartesian multipole tensor given the
   * recursion relationships implemented by the method "Tlmnj", which can be called directly to get a
   * single tensor element. It does not store intermediate values of the recursion, causing it to
   * scale O(order^8). For order = 5, this approach is a factor of 10 slower than recursion.
   */
  @Override
  protected void noStorageRecursion(double[] r, double[] tensor) {
    setR(r);
    source(T000);
    // 1/r
    tensor[0] = T000[0];
    // Find (d/dx)^l for l = 1..order (m = 0, n = 0)
    for (int l = 1; l <= order; l++) {
      tensor[ti(l, 0, 0)] = Tlmnj(l, 0, 0, 0, r, T000);
    }
    // Find (d/dx)^l * (d/dy)^m for l + m = 1..order (m >= 1, n = 0)
    for (int l = 0; l <= o1; l++) {
      for (int m = 1; m <= order - l; m++) {
        tensor[ti(l, m, 0)] = Tlmnj(l, m, 0, 0, r, T000);
      }
    }
    // Find (d/dx)^l * (d/dy)^m * (d/dz)^n for l + m + n = 1..o (n >= 1)
    for (int l = 0; l <= o1; l++) {
      for (int m = 0; m <= o1 - l; m++) {
        for (int n = 1; n <= order - l - m; n++) {
          tensor[ti(l, m, n)] = Tlmnj(l, m, n, 0, r, T000);
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void recursion(double[] r, double[] tensor) {
    setR(r);
    source(work);
    tensor[0] = work[0];
    // Find (d/dx)^l for l = 1..order (m = 0, n = 0)
    // Any (d/dx) term can be formed as
    // Tl00j = x * T(l-1)00(j+1) + (l-1) * T(l-2)00(j+1)
    // All intermediate terms are indexed as l*il + m*im + n*in + j;
    double current;
    double previous = work[1];
    // Store the l=1 tensor T100 (d/dx)
    tensor[ti(1, 0, 0)] = x * previous;
    // Starting the loop at l=2 avoids an if statement.
    for (int l = 2; l < o1; l++) {
      // Initial condition for the inner loop is formation of T100(l-1).
      // Starting the inner loop at a=2 avoid an if statement.
      // T100(l-1) = x * T000(l)
      current = x * work[l];
      int iw = il + l - 1;
      work[iw] = current;
      for (int a = 1; a < l - 1; a++) {
        // T200(l-2) = x * T100(l-1) + (2 - 1) * T000(l-1)
        // T300(l-3) = x * T200(l-2) + (3 - 1) * T100(l-2)
        // ...
        // T(l-1)001 = x * T(l-2)002 + (l - 2) * T(l-3)002
        current = x * current + a * work[iw - il];
        iw += il - 1;
        work[iw] = current;
      }
      // Store the Tl00 tensor (d/dx)^l
      // Tl00 = x * T(l-1)001 + (l - 1) * T(l-2)001
      tensor[ti(l, 0, 0)] = x * current + (l - 1) * previous;
      previous = current;
    }
    // Find (d/dx)^l * (d/dy)^m for l+m = 1..order (m > 0, n = 0)
    // Any (d/dy) term can be formed as:
    // Tlm0j = y * Tl(m-1)00(j+1) + (m-1) * Tl(m-2)00(j+1)
    for (int l = 0; l < order; l++) {
      // Store the m=1 tensor (d/dx)^l *(d/dy)
      // Tl10 = y * Tl001
      previous = work[l * il + 1];
      tensor[ti(l, 1, 0)] = y * previous;
      for (int m = 2; m + l < o1; m++) {
        // Tl10(m-1) = y * Tl00m;
        int iw = l * il + m;
        current = y * work[iw];
        iw += im - 1;
        work[iw] = current;
        for (int a = 1; a < m - 1; a++) {
          // Tl20(m-2) = Y * Tl10(m-1) + (2 - 1) * T100(m-1)
          // Tl30(m-3) = Y * Tl20(m-2) + (3 - 1) * Tl10(m-2)
          // ...
          // Tl(m-1)01 = Y * Tl(m-2)02 + (m - 2) * T(m-3)02
          current = y * current + a * work[iw - im];
          iw += im - 1;
          work[iw] = current;
        }
        // Store the tensor (d/dx)^l * (d/dy)^m
        // Tlm0 = y * Tl(m-1)01 + (m - 1) * Tl(m-2)01
        tensor[ti(l, m, 0)] = y * current + (m - 1) * previous;
        previous = current;
      }
    }
    // Find (d/dx)^l * (d/dy)^m * (d/dz)^n for l+m+n = 1..order (n > 0)
    // Any (d/dz) term can be formed as:
    // Tlmnj = z * Tlm(n-1)(j+1) + (n-1) * Tlm(n-2)(j+1)
    for (int l = 0; l < order; l++) {
      for (int m = 0; m + l < order; m++) {
        // Store the n=1 tensor (d/dx)^l *(d/dy)^m * (d/dz)
        // Tlmn = z * Tlm01
        final int lm = m + l;
        final int lilmim = l * il + m * im;
        previous = work[lilmim + 1];
        tensor[ti(l, m, 1)] = z * previous;
        for (int n = 2; lm + n < o1; n++) {
          // Tlm1(n-1) = z * Tlm0n;
          int iw = lilmim + n;
          current = z * work[iw];
          iw += in - 1;
          work[iw] = current;
          final int n1 = n - 1;
          for (int a = 1; a < n1; a++) {
            // Tlm2(n-2) = z * Tlm1(n-1) + (2 - 1) * T1m0(n-1)
            // Tlm3(n-3) = z * Tlm2(n-2) + (3 - 1) * Tlm1(n-2)
            // ...
            // Tlm(n-1)1 = z * Tlm(n-2)2 + (n - 2) * Tlm(n-3)2
            current = z * current + a * work[iw - in];
            iw += in - 1;
            work[iw] = current;
          }
          // Store the tensor (d/dx)^l * (d/dy)^m * (d/dz)^n
          // Tlmn = z * Tlm(n-1)1 + (n - 1) * Tlm(n-2)1
          tensor[ti(l, m, n)] = z * current + n1 * previous;
          previous = current;
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>This function is a driver to collect elements of the Cartesian multipole tensor. Collecting
   * all tensors scales slightly better than O(order^4).
   *
   * <p>For a multipole expansion truncated at quadrupole order, for example, up to order 5 is
   * needed for energy gradients. The number of terms this requires is binomial(5 + 3, 3) or 8! / (5!
   * * 3!), which is 56.
   *
   * <p>The packing of the tensor elements for order = 1<br>
   * tensor[0] = 1/|r| <br> tensor[1] = -x/|r|^3 <br> tensor[2] = -y/|r|^3 <br> tensor[3] = -z/|r|^3
   * <br>
   *
   * <p>
   * @since 1.0
   */
  @Override
  protected String codeTensorRecursion(final double[] r, final double[] tensor) {
    setR(r);
    source(work);
    StringBuilder sb = new StringBuilder();
    tensor[0] = work[0];
    if (work[0] > 0) {
      sb.append(format("%s = %s;\n", rlmn(0, 0, 0), term(0, 0, 0, 0)));
    }
    // Find (d/dx)^l for l = 1..order (m = 0, n = 0)
    // Any (d/dx) term can be formed as
    // Tl00j = x * T(l-1)00(j+1) + (l-1) * T(l-2)00(j+1)
    // All intermediate terms are indexed as l*il + m*im + n*in + j;
    double current;
    double previous = work[1];
    // Store the l=1 tensor T100 (d/dx)
    tensor[ti(1, 0, 0)] = x * previous;
    sb.append(format("%s = x * %s;\n", rlmn(1, 0, 0), term(0, 0, 0, 1)));
    // Starting the loop at l=2 avoids an if statement.
    for (int l = 2; l < o1; l++) {
      // Initial condition for the inner loop is formation of T100(l-1).
      // Starting the inner loop at a=2 avoid an if statement.
      // T100(l-1) = x * T000(l)
      current = x * work[l];
      int iw = il + l - 1;
      work[iw] = current;
      sb.append(format("double %s = x * %s;\n", term(1, 0, 0, l - 1), term(0, 0, 0, l)));
      for (int a = 1; a < l - 1; a++) {
        // T200(l-2) = x * T100(l-1) + (2 - 1) * T000(l-1)
        // T300(l-3) = x * T200(l-2) + (3 - 1) * T100(l-2)
        // ...
        // T(l-1)001 = x * T(l-2)002 + (l - 2) * T(l-3)002
        current = x * current + a * work[iw - il];
        iw += il - 1;
        work[iw] = current;
        if (a > 1) {
          sb.append(
              format(
                  "double %s = fma(x, %s, %d * %s);\n",
                  term(a + 1, 0, 0, l - a - 1), term(a, 0, 0, l - a), a, term(a - 1, 0, 0, l - a)));
        } else {
          sb.append(
              format(
                  "double %s = fma(x, %s, %s);\n",
                  term(a + 1, 0, 0, l - a - 1), term(a, 0, 0, l - a), term(a - 1, 0, 0, l - a)));
        }
      }
      // Store the Tl00 tensor (d/dx)^l
      // Tl00 = x * [[ T(l-1)001 ]] + (l - 1) * T(l-2)001
      tensor[ti(l, 0, 0)] = x * current + (l - 1) * previous;
      previous = current;
      if (l > 2) {
        sb.append(
            format(
                "%s = fma(x, %s, %d * %s);\n",
                rlmn(l, 0, 0), term(l - 1, 0, 0, 1), (l - 1), term(l - 2, 0, 0, 1)));
      } else {
        sb.append(
            format(
                "%s = fma(x, %s, %s);\n", rlmn(l, 0, 0), term(l - 1, 0, 0, 1),
                term(l - 2, 0, 0, 1)));
      }
    }
    // Find (d/dx)^l * (d/dy)^m for l+m = 1..order (m > 0, n = 0)
    // Any (d/dy) term can be formed as:
    // Tlm0j = y * Tl(m-1)00(j+1) + (m-1) * Tl(m-2)00(j+1)
    for (int l = 0; l < order; l++) {
      // Store the m=1 tensor (d/dx)^l *(d/dy)
      // Tl10 = y * Tl001
      previous = work[l * il + 1];
      tensor[ti(l, 1, 0)] = y * previous;
      sb.append(format("%s = y * %s;\n", rlmn(l, 1, 0), term(l, 0, 0, 1)));
      for (int m = 2; m + l < o1; m++) {
        // Tl10(m-1) = y * Tl00m;
        int iw = l * il + m;
        current = y * work[iw];
        iw += im - 1;
        work[iw] = current;
        sb.append(format("double %s = y * %s;\n", term(l, 1, 0, m - 1), term(l, 0, 0, m)));
        for (int a = 1; a < m - 1; a++) {
          // Tl20(m-2) = Y * Tl10(m-1) + (2 - 1) * T100(m-1)
          // Tl30(m-3) = Y * Tl20(m-2) + (3 - 1) * Tl10(m-2)
          // ...
          // Tl(m-1)01 = Y * Tl(m-2)02 + (m - 2) * T(m-3)02
          current = y * current + a * work[iw - im];
          iw += im - 1;
          work[iw] = current;
          if (a > 1) {
            sb.append(
                format(
                    "double %s = fma(y, %s, %d * %s);\n",
                    term(l, a + 1, 0, m - a - 1),
                    term(l, a, 0, m - a),
                    a,
                    term(l, a - 1, 0, m - a)));
          } else {
            sb.append(
                format(
                    "double %s = fma(y, %s, %s);\n",
                    term(l, a + 1, 0, m - a - 1), term(l, a, 0, m - a), term(l, a - 1, 0, m - a)));
          }
        }
        // Store the tensor (d/dx)^l * (d/dy)^m
        // Tlm0 = y * Tl(m-1)01 + (m - 1) * Tl(m-2)01
        tensor[ti(l, m, 0)] = y * current + (m - 1) * previous;
        previous = current;
        if (m > 2) {
          sb.append(
              format(
                  "%s = fma(y, %s, %d * %s);\n",
                  rlmn(l, m, 0), term(l, m - 1, 0, 1), (m - 1), term(l, m - 2, 0, 1)));
        } else {
          sb.append(
              format(
                  "%s = fma(y, %s, %s);\n",
                  rlmn(l, m, 0), term(l, m - 1, 0, 1), term(l, m - 2, 0, 1)));
        }
      }
    }
    // Find (d/dx)^l * (d/dy)^m * (d/dz)^n for l+m+n = 1..order (n > 0)
    // Any (d/dz) term can be formed as:
    // Tlmnj = z * Tlm(n-1)(j+1) + (n-1) * Tlm(n-2)(j+1)
    for (int l = 0; l < order; l++) {
      for (int m = 0; m + l < order; m++) {
        // Store the n=1 tensor (d/dx)^l *(d/dy)^m * (d/dz)
        // Tlmn = z * Tlm01
        final int lm = m + l;
        final int lilmim = l * il + m * im;
        previous = work[lilmim + 1];
        tensor[ti(l, m, 1)] = z * previous;
        sb.append(format("%s = z * %s;\n", rlmn(l, m, 1), term(l, m, 0, 1)));
        for (int n = 2; lm + n < o1; n++) {
          // Tlm1(n-1) = z * Tlm0n;
          int iw = lilmim + n;
          current = z * work[iw];
          iw += in - 1;
          work[iw] = current;
          sb.append(format("double %s = z * %s;\n", term(l, m, 1, n - 1), term(l, m, 0, n)));
          final int n1 = n - 1;
          for (int a = 1; a < n1; a++) {
            // Tlm2(n-2) = z * Tlm1(n-1) + (2 - 1) * T1m0(n-1)
            // Tlm3(n-3) = z * Tlm2(n-2) + (3 - 1) * Tlm1(n-2)
            // ...
            // Tlm(n-1)1 = z * Tlm(n-2)2 + (n - 2) * Tlm(n-3)2
            current = z * current + a * work[iw - in];
            iw += in - 1;
            work[iw] = current;
            if (a > 1) {
              sb.append(
                  format(
                      "double %s = fma(z, %s, %d * %s);\n",
                      term(l, m, a + 1, n - a - 1),
                      term(l, m, a, n - a),
                      a,
                      term(l, m, a - 1, n - a)));
            } else {
              sb.append(
                  format(
                      "double %s = fma(z, %s, %s);\n",
                      term(l, m, a + 1, n - a - 1),
                      term(l, m, a, n - a),
                      term(l, m, a - 1, n - a)));
            }
          }
          // Store the tensor (d/dx)^l * (d/dy)^m * (d/dz)^n
          // Tlmn = z * Tlm(n-1)1 + (n - 1) * Tlm(n-2)1
          tensor[ti(l, m, n)] = z * current + n1 * previous;
          previous = current;
          if (n > 2) {
            sb.append(
                format(
                    "%s = fma(z, %s, %d * %s);\n",
                    rlmn(l, m, n), term(l, m, n - 1, 1), (n - 1), term(l, m, n - 2, 1)));
          } else {
            sb.append(
                format(
                    "%s = fma(z, %s, %s);\n",
                    rlmn(l, m, n), term(l, m, n - 1, 1), term(l, m, n - 2, 1)));
          }
        }
      }
    }
    return sb.toString();
  }
}