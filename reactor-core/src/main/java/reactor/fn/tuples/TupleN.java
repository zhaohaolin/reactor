/*
 * Copyright (c) 2011-2013 GoPivotal, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.fn.tuples;

/**
 * @author Jon Brisbin
 */
public class TupleN<T1, T2, T3, T4, T5, T6, T7, T8, TRest extends Tuple> extends Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> {

	public TupleN(Object... values) {
		super(values);
	}

	/**
	 * Type-safe way to get the remaining objects of this {@link Tuple}.
	 *
	 * @return The remaining objects, as a Tuple.
	 */
	@SuppressWarnings("unchecked")
	public TRest getTRest() {
		return (TRest) get(8);
	}

}
