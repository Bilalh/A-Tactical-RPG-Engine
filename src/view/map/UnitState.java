package view.map;

import view.units.AnimatedUnit;

/**
 * State Machine for a Unit
 * @author Bilal Hussain
 */
enum UnitState {
	WAITING {
		@Override
		void stateEntered(GuiMap m, AnimatedUnit u) {
		}

		@Override
		void exec(GuiMap m, AnimatedUnit u) {
		}

		@Override
		void stateLeft(GuiMap m, AnimatedUnit u) {
		}
		
	},
	MENU_SELECTED {
		@Override
		void stateEntered(GuiMap m, AnimatedUnit u) {
			// SetupMenu
		}

		@Override
		void exec(GuiMap m, AnimatedUnit u) {
			// display menu?
		}

		@Override
		void stateLeft(GuiMap m, AnimatedUnit u) {
			
		}
	},
	MOVEMENT_RANGE {
		@Override
		void stateEntered(GuiMap m, AnimatedUnit u) {
			// Set up movement range
		}

		@Override
		void exec(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void stateLeft(GuiMap m, AnimatedUnit u) {
			// Remove movement range 
		}
	},
	MENU_MOVED {
		@Override
		void stateEntered(GuiMap m, AnimatedUnit u) {
			// Setup menu
		}

		@Override
		void exec(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void stateLeft(GuiMap m, AnimatedUnit u) {
			
		}
	},
	SHOW_TARGETS {
		@Override
		void stateEntered(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void exec(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void stateLeft(GuiMap m, AnimatedUnit u) {
			
		}
	},
	FIGHT {
		@Override
		void stateEntered(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void exec(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void stateLeft(GuiMap m, AnimatedUnit u) {
			
		}
	},
	FINISHED {
		@Override
		void stateEntered(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void exec(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void stateLeft(GuiMap m, AnimatedUnit u) {
			
		}
	},
	DEFEATED {
		@Override
		void stateEntered(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void exec(GuiMap m, AnimatedUnit u) {
			
		}

		@Override
		void stateLeft(GuiMap m, AnimatedUnit u) {
			
		}
	};

	abstract void stateEntered(GuiMap m, AnimatedUnit u);

	abstract void exec(GuiMap m, AnimatedUnit u);

	abstract void stateLeft(GuiMap m, AnimatedUnit u);
}