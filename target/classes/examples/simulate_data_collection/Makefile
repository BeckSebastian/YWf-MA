
include ../default_script_settings.sh

WORKFLOW_SCRIPT = simulate_data_collection.py

PROSPECTIVE_LINEAGE_DATA = corrected_image

#RETROSPECTIVE_LINEAGE_DATA = sample_id
#RETROSPECTIVE_LINEAGE_VALUE = 'DRT240'
RETROSPECTIVE_LINEAGE_DATA = corrected_image

SCRIPT_RUN_CMD = python simulate_data_collection.py q55 --cutoff 12 --redundancy 1
POST_RUN_CMD = rm -f run/data/DRT240/DRT240_12000eV_002.img

NW_FILTERED_LINEAGE = true

CUSTOM_TARGETS = yw/combined.gv yw/listing.txt \
				yw/xsb/extract_facts.P yw/xsb/model_facts.P yw/xsb/recon_facts.P \
				yw/xsb/run_queries.txt
CUSTOM_CLEAN = yw/xsb/*.xwam

include $(MAKEFILES_DIR)/common.Makefile

yw/combined.gv:
	cd yw; \
	$(YW_CMD) graph

yw/xsb/extract_facts.P yw/xsb/model_facts.P yw/xsb/recon_facts.P:
	cd yw; \
	$(YW_CMD) recon

yw/xsb/run_queries.txt:
	cd yw/xsb; \
	bash run_queries.sh > run_queries.txt
