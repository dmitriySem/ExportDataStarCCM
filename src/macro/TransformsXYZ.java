// Simcenter STAR-CCM+ macro: ss.java
// Written by Simcenter STAR-CCM+ 17.06.007
package macro;

import jeigen.DenseMatrix;
import star.base.neo.NamedObject;
import star.common.*;
import star.energy.HtcUserYPlusFunction;
import static jeigen.Shortcuts.*;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TransformsXYZ extends StarMacro {
  private Simulation sim;
  private double alfa;//угол вращение вокруг (в градусах) вокруг оси X
  private double beta;//угол вращение вокруг (в градусах) вокруг оси Y
  private double gama;//угол вращение вокруг (в градусах) вокруг оси Z
  private String nameFile = "CBK_alfa_T";

  public void execute() {

    sim = getActiveSimulation();

    List<String> surfaces = new ArrayList<>();

    //CBK
	surfaces.add("air_cc_hub_cavity_bolts:wall_cht_cc-impeller_unequal");
	surfaces.add("air_cc_impeller:cht_air_impeller/solid_impeller [0]");
	surfaces.add("air_cc_impeller:hub");
	surfaces.add("air_cc_impeller:hub_inlet_gap");
	surfaces.add("air_cc_impeller:hub_outlet_gap");
	surfaces.add("air_cc_impeller:blade_splitter");
	surfaces.add("air_cc_impeller:blade_main");

    Collection<Region> regions = sim.getRegionManager().getRegions();
    Collection<NamedObject> namedObjectCollection = new ArrayList<>();

    HtcUserYPlusFunction htcUserYPlusFunction =
            ((HtcUserYPlusFunction) sim.getFieldFunctionManager().getFunction("HeatTransferCoefficientUserYPlus"));
    PrimitiveFieldFunction primitiveFieldFunction =
            ((PrimitiveFieldFunction) sim.getFieldFunctionManager().getFunction("HeatTransferReferenceTemperatureUserYPlus"));

    Vector<FieldFunction> fieldFunctionVector = new Vector<>();
    fieldFunctionVector.add(htcUserYPlusFunction);
    fieldFunctionVector.add(primitiveFieldFunction);



    try {
      FileWriter fileWriter = new FileWriter(new File(sim.getSessionDir()+File.separator + "file.txt"));
      PrintWriter printWriter = new PrintWriter(fileWriter);

      for (String surface : surfaces) {
//        sim.println(surface.split(":")[0]);
        NamedObject interfaceBoundary_1 = null;
        String nameReport = "";

        for (Region region : regions) {
          if (region.getPresentationName().equals(surface.split(":")[0])){
//            sim.println("333!!!");
            Optional<Object> OptionalBoundaryt = region.getBoundaryManager().getChildren().stream().
                    filter(boundary -> {
//                      sim.println("bound " + boundary.toString());
//                      sim.println("surf " + surface.split(":")[1]);
//                      sim.println("");
//                      sim.println("");
                      return boundary.toString().contains(surface.split(":")[1]);
                    }).findFirst();
            interfaceBoundary_1 = (NamedObject) OptionalBoundaryt.get();
            namedObjectCollection.add(interfaceBoundary_1);
//            sim.println("444!!!");
            break;
          } else {
            sim.println(String.format("surf %s not found", surface));
          }
        }
      }
      CreateXYZTable(fieldFunctionVector, namedObjectCollection);
      DeliteXYZTable(nameFile);


//        printWriter.append(String.format("\nG,%s,%4.3f\n Gpr,-,%4.3f\n T*,-,%4.3f\n P*,-,%4.3f\n P,-,%4.3f\n",
//                nameReport, massFlowReport_9.getValue(),
//                expressionReport_12.getValue(),
//                massFlowAverageReport_15.getValue(),
//                massFlowAverageReport_16.getValue(),
//                areaAverageReport_7.getValue()));
      printWriter.close();

    } catch (IOException e) {
      sim.println("222!!!");
      e.printStackTrace();
    }
  }


  private void CreateXYZTable(Vector<? extends  FieldFunction> fieldFunctions,
                              Collection <? extends NamedObject> surfs){

    XyzInternalTable xyzInternalTable_0 =
            sim.getTableManager().createTable(XyzInternalTable.class);
    xyzInternalTable_0.setPresentationName(nameFile);
    xyzInternalTable_0.setExtractVertexData(true);
    xyzInternalTable_0.getParts().setObjects(surfs);
    xyzInternalTable_0.setFieldFunctions(fieldFunctions);
    xyzInternalTable_0.extract();
    String WorkPath = sim.getSessionDir() + File.separator;
//    StringBuilder name = new StringBuilder();
//    surfs.forEach(namedObject -> name.append(namedObject.getPresentationName()).append("_"));
    xyzInternalTable_0.export(WorkPath + nameFile + ".csv", ",");
  }

  private void DeliteXYZTable(String name){
    XyzInternalTable xyzInternalTable_0 =
            ((XyzInternalTable) sim.getTableManager().getTable(name));
    sim.getTableManager().remove(xyzInternalTable_0);

  }

  private void DeliteXYZTable(XyzInternalTable xyzInternalTable_0){
    sim.getTableManager().remove(xyzInternalTable_0);
  }

  private void transformXYZ(){
    DenseMatrix rotX = new DenseMatrix(new double[][]{
            {1, 0, 0},
            {0, Math.cos(Math.toRadians(alfa)), -Math.sin(Math.toRadians(alfa))},
            {0, Math.sin(Math.toRadians(alfa)), Math.cos(Math.toRadians(alfa))}
    });
    DenseMatrix rotY = new DenseMatrix(new double[][]{
            {Math.cos(Math.toRadians(beta)), 0, Math.sin(Math.toRadians(beta))},
            {0, 1, 0},
            {-Math.sin(Math.toRadians(beta)), 0, Math.cos(Math.toRadians(beta))}
    });

    DenseMatrix r = rotY.mmul(rotX);

  }
}
