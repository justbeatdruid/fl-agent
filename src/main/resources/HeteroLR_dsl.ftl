{
	"components" : {
        "dataio_0": {
            "module": "DataIO",
            "input": {
                "data": {
                    "data": [
                        "args.train_data"
                    ]
                }
            },
            "output": {
                "data": ["train"],
                "model": ["dataio"]
            }
        },
		"intersection_0": {
             "module": "Intersection",
             "input": {
                 "data": {
                     "data": [
                         "dataio_0.train"
                     ]
                 }
             },
             "output": {
                 "data": ["train"]
             }
        },
        "algorithm_0": {
            "module": "HeteroLR",
            "input": {
                "data": {
                    "train_data": [
                        "intersection_0.train"
                    ]
                }
            },
            "output": {
                "data": ["train"],
                "model": ["model"]
            }
        },
        "evaluation_0": {
            "module": "Evaluation",
            "input": {
                "data": {
                    "data": [
                        "algorithm_0.train"
                    ]
                }
            }
        }
    }
}
