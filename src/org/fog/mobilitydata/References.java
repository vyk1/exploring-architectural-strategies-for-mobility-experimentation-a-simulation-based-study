package org.fog.mobilitydata;

import java.io.File;

public class References {

	public static final int NOT_SET = -1;
	public static final double SETUP_TIME = -1.00;
	public static final double INIT_TIME = 0.00;

	public static final int DIRECTIONAL_MOBILITY = 1;
	public static final int RANDOM_MOBILITY = 2;

	// Reference geographical information to create random mobility pattern for
	// mobile users

	public static boolean is_ine_experiment = false;

	public static boolean is_ufsc_experiment = false;

	public static boolean is_ops_experiment = false;

	public static String[] comparative_mobility_patterns = { "1", "2", "3" };

	public static final double[] ine_starting_point_reference = { -27.60057f, -48.51859f };

//	[north reference, south reference]
	public static final double[][] ens_starting_point_references = { { -27.6004, -48.51828 }, { -27.60052, -48.5183 } };

	public static final String output_path_data = String.format(".%sdataset%soutput_path_data.csv", File.separator,
			File.separator);
	// Reference dataset filename to store and retrieve users positions
//	public static final String dataset_reference = String.format(".%sdataset%slocation%sstatic%susersLocation_", File.separator, File.separator, File.separator,
//			File.separator);
	public static final String dataset_reference = is_ufsc_experiment
			? is_ine_experiment
					? String.format(".%sdataset%sofficial%sine%srandom_usersLocation-melbCBD_", File.separator,
							File.separator, File.separator, File.separator)
					: String.format(".%sdataset%sofficial%sens%srandom_usersLocation-melbCBD_", File.separator,
							File.separator, File.separator, File.separator)
			: is_ops_experiment
					? String.format(".%sdataset%sofficial%scomparative%sops%s%s%susersLocation-melbCBD_",
							File.separator, File.separator, File.separator, File.separator, File.separator,
							comparative_mobility_patterns[0], File.separator)
					: String.format(".%sdataset%sofficial%scomparative%sips%s%s%susersLocation-melbCBD_",
							File.separator, File.separator, File.separator, File.separator, File.separator,
							comparative_mobility_patterns[0], File.separator);

	public static final String dataset_random = String.format(".%sdataset%srandom_usersLocation-melbCBD_",
			File.separator, File.separator);
	public static final int random_walk_mobility_model = 2;
	public static final int random_waypoint_mobility_model = 2;
	public static double MinMobilitySpeed = 1; //
	public static double MaxMobilitySpeed = 2; //
	public static double environmentLimit = 6371; // shows the maximum latitude and longitude of the environment.
													// Currently it is set based on radius of the Earth (6371 KM)
}
