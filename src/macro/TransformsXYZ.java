// Simcenter STAR-CCM+ macro: ss.java
// Written by Simcenter STAR-CCM+ 17.06.007
package macro;

import star.base.neo.NamedObject;
import star.base.neo.NeoObjectVector;
import star.base.report.AreaAverageReport;
import star.base.report.ExpressionReport;
import star.base.report.Report;
import star.common.*;
import star.flow.MassFlowAverageReport;
import star.flow.MassFlowReport;
import star.post.SolutionRepresentation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class TransformsXYZ extends StarMacro {

  public void execute() {

    Simulation simulation_0 =
            getActiveSimulation();

    List<String> surfaces = new ArrayList<>();

    //CBK
	surfaces.add("air_cc_hub_cavity_bolts:wall_cht_cc-impeller_unequal");
	surfaces.add("air_cc_impeller:cht_air_impeller/solid_impeller [0]");
	surfaces.add("air_cc_impeller:hub");
	surfaces.add("air_cc_impeller:hub_inlet_gap");
	surfaces.add("air_cc_impeller:hub_outlet_gap");
	surfaces.add("air_cc_impeller:blade_splitter");
	surfaces.add("air_cc_impeller:blade_main");


    try {
      FileWriter fileWriter = new FileWriter(new File(simulation_0.getSessionDir()+File.separator + "file.txt"));
      PrintWriter printWriter = new PrintWriter(fileWriter);

      Collection<Region> regions = simulation_0.getRegionManager().getRegions();
      Collection<NamedObject> namedObjectCollection = new ArrayList<>();
      for (String surface : surfaces) {
//        simulation_0.println(surface.split(":")[0]);
        NamedObject interfaceBoundary_1 = null;
        String nameReport = "";

        for (Region region : regions) {
          if (region.getPresentationName().equals(surface.split(":")[0])){
//            simulation_0.println("333!!!");
            Optional<Object> OptionalBoundaryt = region.getBoundaryManager().getChildren().stream().
                    filter(boundary -> {
//                      simulation_0.println("bound " + boundary.toString());
//                      simulation_0.println("surf " + surface.split(":")[1]);
//                      simulation_0.println("");
//                      simulation_0.println("");
                      return boundary.toString().contains(surface.split(":")[1]);
                    }).findFirst();
            interfaceBoundary_1 = (NamedObject) OptionalBoundaryt.get();
            namedObjectCollection.add(interfaceBoundary_1);
//            simulation_0.println("444!!!");
            break;
          } else {
            simulation_0.println(String.format("surf %s not found", surface));
          }
        }


//        printWriter.append(String.format("\nG,%s,%4.3f\n Gpr,-,%4.3f\n T*,-,%4.3f\n P*,-,%4.3f\n P,-,%4.3f\n",
//                nameReport, massFlowReport_9.getValue(),
//                expressionReport_12.getValue(),
//                massFlowAverageReport_15.getValue(),
//                massFlowAverageReport_16.getValue(),
//                areaAverageReport_7.getValue()));
      }
      printWriter.close();

    } catch (IOException e) {
      simulation_0.println("222!!!");
      e.printStackTrace();
    }
  }


  private void CreateXYZTable( PrimitiveFieldFunction primitiveFieldFunction_0,
                               Collection <? extends NamedObject> region_0){

    XyzInternalTable xyzInternalTable_0 =
            sim.getTableManager().createTable(XyzInternalTable.class);
    xyzInternalTable_0.setExtractVertexData(true);
    xyzInternalTable_0.setRepresentation(solutionRepresentation_0);
    xyzInternalTable_0.getParts().setObjects(region_0);
    xyzInternalTable_0.setFieldFunctions(new NeoObjectVector(new Object[] {primitiveFieldFunction_0}));
    xyzInternalTable_0.setCoordinateSystem(cylindricalCoordinateSystem_0);
    xyzInternalTable_0.extract();
    String WorkPath = sim.getSessionDir() + File.separator;
    StringBuilder name = new StringBuilder();
    region_0.forEach(namedObject -> name.append(namedObject.getPresentationName()).append("_"));
    xyzInternalTable_0.export(WorkPath + name + ".csv", ",");
  }

  private void DeliteXYZTable(Simulation simulation_0, String name){
    XyzInternalTable xyzInternalTable_0 =
            ((XyzInternalTable) simulation_0.getTableManager().getTable(name));
    simulation_0.getTableManager().remove(xyzInternalTable_0);

  }

  private void DeliteXYZTable(XyzInternalTable xyzInternalTable_0){
    sim.getTableManager().remove(xyzInternalTable_0);
  }
}
