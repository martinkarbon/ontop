package it.unibz.krdb.obda.protege4.gui.view;

import it.unibz.krdb.obda.gui.swing.panel.DatasourceSelector;
import it.unibz.krdb.obda.gui.swing.panel.MappingManagerPanel;
import it.unibz.krdb.obda.model.DataSource;
import it.unibz.krdb.obda.model.DatasourcesController;
import it.unibz.krdb.obda.model.MappingController;
import it.unibz.krdb.obda.model.OBDAModel;
import it.unibz.krdb.obda.model.impl.OBDAModelImpl;
import it.unibz.krdb.obda.protege4.core.OBDAPluginController;
import it.unibz.krdb.obda.utils.OBDAPreferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owl.model.OWLOntology;

public class MappingsManagerView extends AbstractOWLViewComponent {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private static final Logger log = Logger.getLogger(MappingsManagerView.class);
       
  @Override
  protected void disposeOWLView() {
    // Do nothing.
  }

  @Override
  protected void initialiseOWLView() throws Exception {
    // Retrieve the editor kit.
    final OWLEditorKit editor = getOWLEditorKit();
    
    OBDAPluginController controller = editor.get(OBDAModelImpl.class.getName());
    OBDAPreferences preference = (OBDAPreferences) editor.get(OBDAPreferences.class.getName());
    
    // Retrieve the components for initializing the Mapping Manager panel.
    OBDAModel obdaModel = controller.getOBDAManager();
    MappingController mapController = obdaModel.getMappingController();
    DatasourcesController dsController = obdaModel.getDatasourcesController();
    OWLOntology ontology = editor.getModelManager().getActiveOntology();
    
    // Init the Mapping Manager panel.
    MappingManagerPanel mappingPanel = new MappingManagerPanel(obdaModel, mapController, dsController, preference, ontology); 
    
    // Init the Data source Selector.
    Vector<DataSource> vecDatasource = new Vector<DataSource>(dsController.getAllSources());
    DatasourceSelector datasourceSelector = new DatasourceSelector(vecDatasource);
    datasourceSelector.addDatasourceListListener(mappingPanel);
    dsController.addDatasourceControllerListener(datasourceSelector);
    
    // Construt the layout of the panel.
    JPanel selectorPanel = new JPanel();
    selectorPanel.setLayout(new GridBagLayout());
    
    JLabel label = new JLabel("Select datasource: ");
    label.setBackground(new java.awt.Color(153, 153, 153));
    label.setFont(new java.awt.Font("Arial", 1, 11));
    label.setForeground(new java.awt.Color(153, 153, 153));
    label.setPreferredSize(new Dimension(119,14));
    
    GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new Insets(5, 5, 5, 5);
    selectorPanel.add(label, gridBagConstraints);
    
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new Insets(5, 5, 5, 5);
    selectorPanel.add(datasourceSelector, gridBagConstraints);
    
    selectorPanel.setBorder(new TitledBorder("Datasource Selection"));
    mappingPanel.setBorder(new TitledBorder("Mapping Inspector"));
    
    setLayout(new BorderLayout());
    add(mappingPanel, BorderLayout.CENTER);
    add(selectorPanel, BorderLayout.NORTH);
      
    log.debug("Mappings manager initialized");
  }
}
